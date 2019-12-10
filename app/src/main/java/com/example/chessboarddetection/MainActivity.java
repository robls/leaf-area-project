package com.example.chessboarddetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCamera2View;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.*;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;


import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import SQLHelper.Helper;

public class MainActivity extends AppCompatActivity implements
    CameraBridgeViewBase.CvCameraViewListener2 {

     CameraBridgeViewBase cameraBridgeViewBase;
     BaseLoaderCallback baseLoaderCallback;
     Button botaoEfeito;
     Button botaoSnapshot;
     Button botaoCalcula;
     List<Mat> imagePoints;
     List<Mat> objectPoints;
     MatOfPoint3f obj;
     MatOfPoint2f imageCorners;
     int boardsNumber;
     int numCornersHor;
     int numCornersVer;
     int successes;
     Mat intrinsic;
     Mat savedImage;
     Mat distCoeffs;
     Image undistortedImage;
     boolean isCalibrated;
     boolean isEfeito;
     boolean calcula;
     boolean jaCalibrou;
     TextView textoSucessos;
     TextView isCalibrado;
     TextView tvContPixelsBrancos;
     ImageView imgView;
     double comprimento;
     double largura;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OpenCVLoader.initDebug();
        this.boardsNumber = 30;
        this.numCornersHor = 9;
        this.numCornersVer = 6;
        int numSquares = this.numCornersHor * this.numCornersVer;
        this.obj = new MatOfPoint3f();
        for (int j = 0; j < numSquares; j++)
            obj.push_back(new MatOfPoint3f(new Point3(j / this.numCornersHor,
                    j % this.numCornersVer, 0.0f)));
        this.imageCorners = new MatOfPoint2f();
        this.imagePoints = new ArrayList<>();
        this.objectPoints = new ArrayList<>();
        this.intrinsic = new Mat(3, 3, CvType.CV_32FC1);
        this.distCoeffs = new Mat();
        this.savedImage = new Mat();
        this.successes = 0;
        this.isCalibrated = false;
        this.undistortedImage = null;
        Intent myIntent = getIntent();
        this.comprimento = myIntent.getDoubleExtra("comprimento", 0);
        this.largura = myIntent.getDoubleExtra("largura", 0);


        textoSucessos = findViewById(R.id.num_sucessos);
        isCalibrado = findViewById(R.id.is_calibrado);
        tvContPixelsBrancos = findViewById(R.id.tvBrancos);
        textoSucessos.setText("0 / " + this.boardsNumber + " Snapshots");
        botaoEfeito = findViewById(R.id.botaoEfeito);
        botaoCalcula = findViewById(R.id.botaoCalculaBrancos);
        botaoSnapshot = findViewById(R.id.snapshot_button);
        cameraBridgeViewBase = (JavaCameraView)findViewById(R.id.CameraView);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);
        imgView = findViewById(R.id.ivSnapshot);

        Helper sql = new Helper(this,"coefs",null,1);
        if(sql.isDbEmpty()){
            jaCalibrou = true;
            this.distCoeffs = sql.dbget("distcoeffs");
            this.intrinsic = sql.dbget("intrinsic");
            isCalibrado.setText("LENTE CALIBRADA");
            isCalibrado.setTextColor(Color.GREEN);
            Toast.makeText(getApplicationContext(), "Camera ja foi calibrada.", Toast.LENGTH_LONG).show();
        }else{
            jaCalibrou = false;
        }

        botaoSnapshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeSnapshot();
            }
        });

        botaoCalcula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calcula = true;
            }
        });

        botaoEfeito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEfeito = true;
            }
        });

        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                super.onManagerConnected(status);
                switch(status){
                    case BaseLoaderCallback.SUCCESS:
                        cameraBridgeViewBase.enableView();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };
    }

    protected void takeSnapshot(){
        if (this.successes < this.boardsNumber){
            if(this.imageCorners.empty()){
                Toast.makeText(getApplicationContext(), "VAZIO", Toast.LENGTH_LONG).show();
            }else{
                // armazena todos os valores necessarios para a funcao de calibrateCamera
                this.imagePoints.add(imageCorners);
                this.imageCorners = new MatOfPoint2f();
                this.objectPoints.add(obj);
                this.successes++;
                textoSucessos.setText(Integer.toString(successes) + " / " + this.boardsNumber +
                        " Snapshots");
            }
        }
        // qaundo numero de snapshots for igual ao necessario
        if (this.successes == this.boardsNumber){
            this.calibrateCamera();
        }
    }

    private void calibrateCamera(){
        // inicializa as variaveis
        List<Mat> rvecs = new ArrayList<>();
        List<Mat> tvecs = new ArrayList<>();
        intrinsic.put(0, 0, 1);
        intrinsic.put(1, 1, 1);
        // invoca a funcao de calibracao, obtendo as matrizes de valores intrinsic e os
        // coeficientes de distancia que serao utilizadas na funcao undistort
        Calib3d.calibrateCamera(objectPoints, imagePoints, savedImage.size(), this.intrinsic,
                this.distCoeffs, rvecs, tvecs);
        Helper sql = new Helper(this,"coefs",null,1);
        sql.dbput("intrinsic", this.intrinsic);
        Log.d("dbhelper", this.distCoeffs.toString());
        sql.dbput("distcoeffs", this.distCoeffs);
        sql.isDbEmpty();
        isCalibrado.setText("LENTE CALIBRADA!");
        isCalibrado.setTextColor(Color.GREEN);
        this.isCalibrated = true;
    }

    public Bitmap mat2bitmap(Mat frame){
        Bitmap result =  Bitmap.createBitmap(frame.width(),frame.height(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(frame,result);
        return result;
    }

    private void findAndDrawPoints(Mat frame){
        Mat grayImage = new Mat();
        if (this.successes < this.boardsNumber){
            // conversao do quadro para escala de cinza
            Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY);
            // tamanho do tabuleiro
            Size boardSize = new Size(this.numCornersHor, this.numCornersVer);
            // procura os cantos internos do tabuleiro
            // alternativamente, caso se queira valores maiores de FPS, pode se desabilitar
            // as opcoes de Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE
            boolean found = Calib3d.findChessboardCorners(grayImage, boardSize, imageCorners,
                    Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE
                            + Calib3d.CALIB_CB_FAST_CHECK);
            Log.d("tag", String.valueOf(found));
            Log.d("er1", imageCorners.toString());
            //todos os "corners" do padrao de xadrez foram encontrados
            if (found){
                // otimizacao
                if(imageCorners.empty()){
                    Log.d("ER1", "IMAGE CORNERS VAZIO");
                }else{
                    TermCriteria term = new TermCriteria(TermCriteria.EPS |
                            TermCriteria.MAX_ITER,30, 0.1);
                    // funcao cornerSubPix refina a precisao dos cantos encontrados
                    Imgproc.cornerSubPix(grayImage, imageCorners, new Size(11, 11),
                            new Size(-1, -1), term);
                    // salva o quadro atual para procedimentos futuros
                    grayImage.copyTo(savedImage);
                    // renderiza os cantos interiores encontrados na tela
                    Calib3d.drawChessboardCorners(frame, boardSize, imageCorners, found);
                }
            }
        }
    }


    //Calcula a porcentagem de pixels brancos dado o tamanho do tabuleiro
    private void calculaBrancos(final Mat frame){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                calcula = false;
                int contPixelsBrancos = Core.countNonZero(frame);
                double totalPixelsImg = 409600;
                double porcentagemDeBrancosAoTotal = (contPixelsBrancos/totalPixelsImg);
                double areaTotal = comprimento * largura;
                double areaBranco = areaTotal * porcentagemDeBrancosAoTotal;
                tvContPixelsBrancos.setText(" Area da Folha = " + areaBranco +" cm");
                Bitmap bitmapFrame = mat2bitmap(frame);
                imgView.setImageBitmap(bitmapFrame);
                cameraBridgeViewBase.disableView();
                cameraBridgeViewBase.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat frame = inputFrame.rgba();
        Mat mRgba = inputFrame.rgba();
        if(isCalibrated || jaCalibrou){
            Core.flip(frame.t(), frame, 1);
            Imgproc.resize(frame, frame, mRgba.size());
            Mat undistorted = new Mat();
            frame.copyTo(undistorted);
            Imgproc.undistort(frame, undistorted,  this.intrinsic,  this.distCoeffs);
            if(isEfeito){
                Mat hsv = new Mat();
                Imgproc.cvtColor(undistorted, hsv, Imgproc.COLOR_RGB2HSV);
                Integer sens = 20;
                Scalar limitesMinimos = new Scalar(60 - sens, 60, 60);
                Scalar limitesMaximos = new Scalar(60 + sens, 255, 255);
                Mat thresh = new Mat();
                //inRange(Mat src, Scalar lowerBound, Scalar upperBound, Mat dest).
                Core.inRange(hsv, limitesMinimos, limitesMaximos, thresh);
                if(calcula){
                    calculaBrancos(thresh);
                }
                return thresh;
            }
            return undistorted;
        }else{
            Core.flip(frame.t(), frame, 1);
            Imgproc.resize(frame, frame, mRgba.size());
            findAndDrawPoints(frame);
            return frame;
        }
    }


    @Override
    public void onCameraViewStarted(int width, int height) {

    }


    @Override
    public void onCameraViewStopped() {

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()){
            Toast.makeText(getApplicationContext(),"OpenCV não inicializado!",
                    Toast.LENGTH_SHORT).show();
        }

        else{
            baseLoaderCallback.onManagerConnected(baseLoaderCallback.SUCCESS);
        }



    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.limpardados:
                Toast.makeText(this, "Dados da camêra foram limpados.", Toast.LENGTH_SHORT).show();
                Helper sql = new Helper(this,"coefs",null,1);
                sql.limpaTable();
                isCalibrado.setText("LENTE NAO CALIBRADA!");
                isCalibrado.setTextColor(Color.RED);
                this.isCalibrated = false;
                jaCalibrou = false;
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}