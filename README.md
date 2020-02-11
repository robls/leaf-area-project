# projetoAreaFoliar
EN:
This work aims to satisfactorily calculate the area of ​​a leaf using a mobile application for Android. For that, an image manipulation library called OpenCV was used, such library was used in version 3.4.5. It provides, in a simplified way, functions that will allow the manipulation of the necessary images.

Briefly, the application removes distortions pertaining to the camera lens of a specific device. Initially, if the device has not been calibrated yet, the application will process the information from the camera frame by frame in order to physically find the chess board on the device screen. This board will be able to provide spatial distortion information through a certain number of samples (snapshots), which in this case are pictures of the board from different angles and positions.

Once snapshots are satisfactorily obtained, the application invokes the calibrateCamera () function, provided by the OpenCV library, and obtains the necessary parameters to perform the removal of lens distortion. Then, the undistort () function is applied to all future frames provided by the camera, thus enabling the user to place the sheet on the tray to proceed with the application's execution.

It is now possible to segment the shape of the sheet in relation to the rest of the tray, it is important to note that it is necessary to center the tray on the camera frame present on the user's screen. From the values ​​of the dimension of the board (height and length) and the percentage of white pixels present in the image, it is possible to calculate approximately the area of ​​the sheet.

The storage of the parameters required to call the undistort () function is done by SQLite and allows the calibration process to be done only once, if necessary.

Tools used: OpenCV 3.4.5, SQLite and Android Studio 3.5.2.

Main functions used by the program: findAndDrawPoints (), this function finds the interior points of a chessboard and draws them on the screen for the user;

takeSnapshot (), this function stores the information needed to call the calibrateCamera () function;

calibrateCamera (), this function returns the distortion coefficients obtained through the values collected by the snapshots. Values ​​such as the distance coefficient between spatial points and the intrinsically distorted matrix of that lens;

calculaBrancos (), this function uses the dimensions of the board to calculate the percentage of white pixels after the segmentation of the image obtained by the camera;

cleanTable (), allows the user to clean data that was collected incorrectly.

--------------------------------------------------------------------------------
PT-BR:
Este trabalho objetiva calcular de maneira satisfatória a área de uma folha por meio de um aplicativo móvel para Android. Para isso foi utilizada uma biblioteca de manipulação de imagens denominada OpenCV, tal biblioteca foi utilizada em sua versão 3.4.5. Ela disponibiliza, de maneira simplificada, funções que irão permitir a manipulação das imagens necessárias.

Resumidamente, o aplicativo remove as distorções pertencentes à lente da câmera de um aparelho específico. Inicialmente, caso o aparelho não tenha sido calibrado ainda, o aplicativo irá processar quadro por quadro as informações provenientes da câmera com o intuito de encontrar fisicamente o tabuleiro de xadrez na tela do aparelho. Esse tabuleiro será capaz de fornecer as informações espaciais de distorção por meio de uma determinada quantidade de amostras (snapshots), que nesse caso são fotos do tabuleiro de diferentes ângulos e posições.

Uma vez obtidos de maneira satisfatória os snapshots, o aplicativo invoca a função calibrateCamera(), fornecida pela biblioteca OpenCV, e obtém os parâmetros necessários para realizar a remoção da distorção da lente. Em seguida, a função undistort() é aplicada todos os futuros quadros fornecidos pela câmera possibilitando então que o usuário possa colocar a folha no tabuleiro para prosseguir com a execução do aplicativo.

Agora é possível segmentar a forma da folha em relação ao restante do tabuleiro, é importante salientar que é necessário centralizar o tabuleiro no quadro da câmera presente na tela do usuário. A partir dos valores de dimensão do tabuleiro (altura e comprimento) e da porcentagem de pixels brancos presentes na imagem, é possível calcular aproximadamente a área da folha.

O armazenamento dos parâmetros necessários para chamada da função undistort() é feito pelo SQLite e permitem que o processo de calibramento seja feito apenas uma vez, caso necessário.

Ferramentas utilizadas: OpenCV 3.4.5, SQLite e Android Studio 3.5.2. 

Principais funções utilizadas pelo programa: 
  findAndDrawPoints(), essa função encontra os pontos interiores de um tabuleiro de xadrez e os desenha na tela para o usuário;
  
  takeSnapshot(), essa função realiza o armazenamento das informações necessárias para a chamada da função calibrateCamera();
  
  calibrateCamera(), essa função retorna os coeficientes de distorção obtidos por meio dos valores coletados pelos snapshots. Valores como o coeficiente de distância entre os pontos espaciais e o matriz de distorção ìntrinsica aquela lente;
  
  calculaBrancos(), essa função utiliza as dimensões do tabuleiro para calcular a porcentagem de pixels brancos após a segmentação da imagem obtida pela câmera;
  
  limpaTable(), permite ao usuário limpar dados que foram coletados erroneamente.
