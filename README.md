# projetoAreaFoliar
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
