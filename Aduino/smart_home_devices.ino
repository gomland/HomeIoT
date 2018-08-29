#include <Servo.h> 

#define LED_RED 13
#define LED_YELLOW 8
#define LED_GREEN 12

#define MORTOR 11
#define BEEP 4
#define SW_DOOR 7

#define NOTE  880
#define NOTE_DOOR 46

String inputString = "";         // a string to hold incoming data
boolean stringComplete = false;  // whether the string is complete

boolean doorClick = false;

Servo servo; 
int angle = 0;

void setup() {
  Serial.begin(9600);
  inputString.reserve(200);

  servo.attach(MORTOR);
  servo.write(0); 
  
  pinMode(LED_RED, OUTPUT);
  pinMode(LED_YELLOW, OUTPUT);
  pinMode(LED_GREEN, OUTPUT);
  pinMode(SW_DOOR, INPUT_PULLUP);
}

void loop() {
  if(digitalRead(SW_DOOR) == LOW && !doorClick){
    doorClick = true;
    Serial.print("E");    
    tone(BEEP, NOTE_DOOR, 200);
    delay(250);
    tone(BEEP, NOTE_DOOR, 200);
    delay(250);
    tone(BEEP, NOTE_DOOR, 200);
  }
  else if(digitalRead(SW_DOOR) == HIGH)
    doorClick = false;
    
    
  if(stringComplete) {
    //Serial.println(inputString);
    setControll(inputString);
    inputString = "";
    stringComplete = false;
  }
}

void doorOpen(){
  domisol();
  for(angle = 0; angle <= 130; angle++) { 
    servo.write(angle); 
    delay(8); 
  } 
}

void doorClose(){
  solmido();
  for(angle = 130; angle >= 0; angle--) { 
    servo.write(angle); 
    delay(8); 
  } 
}

void setControll(String str){
  if(str[0] == '1'){  //11 or 10
    if(str[1] == '1'){
      digitalWrite(LED_RED, HIGH);
      beep(2);
    }
    else{
      digitalWrite(LED_RED, LOW);
      beep(1);
    }
  }
  else if(str[0] == '2'){
    if(str[1] == '1'){
      digitalWrite(LED_YELLOW, HIGH);
      beep(2);
    }
    else{
      digitalWrite(LED_YELLOW, LOW);
      beep(1);
    }
  }
  else if(str[0] == '3'){
    if(str[1] == '1'){
      digitalWrite(LED_GREEN, HIGH);
      beep(2);
    }
    else{
      digitalWrite(LED_GREEN, LOW);
      beep(1);
    }
  }
  else if(str[0] == '6'){
    if(str[1] == '1')
      doorOpen();
    else
      doorClose();
  }
}

void beep(int repeat){
  int i;
  for(i=0; i<repeat; i++){
    tone(BEEP, NOTE, 200);
    delay(250);
  }
}

void domisol(){
  tone(BEEP, 523, 200);
  delay(250);
  tone(BEEP, 659, 200);
  delay(250);
  tone(BEEP, 784, 200);
  delay(250);
}


void solmido(){
  tone(BEEP, 784, 200);
  delay(250);
  tone(BEEP, 659, 200);
  delay(250);
  tone(BEEP, 523, 200);
  delay(250);
}

void serialEvent() {
  while (Serial.available()) {
    char inChar = (char)Serial.read();
    inputString += inChar;

    if (inChar == '\n') {
      stringComplete = true;
    }
  }
}


