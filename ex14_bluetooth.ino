#include <SoftwareSerial.h>    //블루투스 시리얼 통신 라이브러리 추가
SoftwareSerial BTSerial(2, 3); //블루투스 설정 BTSerial(Tx, Rx)

int Red = 5;

void setup() {
  BTSerial.begin(9600); //블루투스 통신 시작
  pinMode(Red, OUTPUT); 
  digitalWrite(5, LOW);
}

void loop() {
  // TEST TEST

  if(BTSerial.available())        //값이 들어오면
  {
    char bt;                     //제어할 변수 bt선언
    bt = BTSerial.read();        //들어온 값을 bt에 저장
    case 0 : digitalWrite(5, LOW); break;
    case 1 : digitalWrite(5, HIGH); break;
  }
}
