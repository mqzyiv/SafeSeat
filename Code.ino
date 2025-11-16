
#include <math.h>
#include "Air_Quality_Sensor.h"
#include "WiFiS3.h"
#include "arduino_secrets.h" 

const int B = 4275;            // B value of the thermistor
const int R0 = 100000;            // R0 = 100k
const int pinTempSensor = A0;     // Grove - Temperature Sensor connect to A0


#define debug Serial
AirQualitySensor sensor(A1);
  
WiFiServer server(80);
int status = WL_IDLE_STATUS;
int count = 0;
const int arr[] = {1000,1100,1200,1300,1400,1500,1600,1700,1800,1900,2000,2100,2200,2300,2400,2500,2600,2700,2800,2900,3000,3100,3200,3300,3400,3500,3600,3700,3800,3900,4000,4100,4200,4300,4400,4500,4600,4700,4800,4900,5000,5100,5200,5300,5400,5500,5600,5700,5800,5900,6000,6100,6200,6300,6400,6500,6600,6700,6800,6900,7000};
int alarm(int num){
  num = num%33;
  return arr[num];
}
void setup() {
  // put your setup code here, to run once:
    debug.begin(9600);
    pinMode(2, OUTPUT);
    while (status != WL_CONNECTED) {
      debug.println(status); 
      status = WiFi.begin(SECRET_SSID, SECRET_PASS); //Arduino UNO R4 uses 2.4 GHz
      delay(10000);
    }
    server.begin();                           // start the web server on port 80
  
    debug.println(WiFi.localIP());  
    debug.println("Im in");

}

void loop() {
  // put your main code here, to run repeatedly:
  int a = analogRead(pinTempSensor);
  float R = 1023.0/a-1.0;
  R = R0*R;
  float temp = 1.0/(log(R/R0)/B+1/298.15)-273.15; // convert to temperature via datasheet
  int quality = sensor.slope();
  
  if (temp>28|| quality == AirQualitySensor::FORCE_SIGNAL||quality == AirQualitySensor::HIGH_POLLUTION){
    count+=1;
    tone(2,alarm(count),1000);
    debug.println("ISSUE");
  }
  WiFiClient client = server.available();   
  if(client){
    while (client.connected()&&client.available()){
      client.read();             
    }
    client.println("HTTP/1.1 200 OK");//this is to make sure browser connects
    client.println("Content-Type: text/html"); 
    client.println("Connection: close");
    client.println();  //IMPORTANT blank line
    client.println("{\"temp\":");
    client.print(temp);
    client.print(", \"airquality\":");
    client.print(quality);
    client.print("}");

    debug.println("here");
    delay(1);
    client.stop();
  }
  
  
}
