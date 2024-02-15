#include <Servo.h>//For tone
#include <pwmWrite.h>//for tone
#include <HTTPClient.h>
#include <ArduinoJson.h>
#include "LiquidCrystal_I2C.h"
#include <DHTesp.h>

#define RED 13
#define GREEN 27
#define BLUE 25

#define Device_UID "wmoralesDevice001"
#define server "http://192.168.0.243:5000"
#define ssid "VPNet5183"
#define password "12345678"

LiquidCrystal_I2C lcd(0x27,16,2);
DHTesp dht;
Servo myservo = Servo();

void postRequest(int ppm, float temp, float hum, String danger) {
  HTTPClient client;
  String endpoint = String(server) + "/device_monitor";

  DynamicJsonDocument jsonDocument(256);
  jsonDocument["ppm"] = String(ppm);
  jsonDocument["temp"] = String(temp) + "F";
  jsonDocument["hum"] = String(hum) + "%";
  jsonDocument["danger"] = danger;
  jsonDocument["device_uid"] = Device_UID;
  String jsonPayload;
  serializeJson(jsonDocument, jsonPayload);

  client.begin(endpoint);
  client.addHeader("Content-Type", "application/json");

  int responseCode = client.POST(jsonPayload);

  if (responseCode > 0) {
    String serverResponse = client.getString();
    Serial.println("Server Response: " + serverResponse);
  } else {
    Serial.println("Response not received.");
  }

  client.end();
}
void setup() 
{
  Serial.begin(9600);
  lcd.init();
  lcd.backlight();
  lcd.print("Initializing! :)");
  dht.setup(32, DHTesp::DHT11); // GPIO15
  pinMode(15, OUTPUT); //MQ SENSOR
  pinMode(36, OUTPUT); //Piezo

  pinMode(RED, OUTPUT); // RGB
  pinMode(GREEN, OUTPUT); // RGB
  pinMode(BLUE, OUTPUT); // RGB

  WiFi.begin(ssid, password);
  uint32_t notConnectedCounter = 0;
  while (WiFi.status() != WL_CONNECTED) {
    delay(100);
    Serial.println("Connecting to WiFi...");
    notConnectedCounter++;
    if(notConnectedCounter > 150) { // Reset board if not connected after 15s
      Serial.println("Resetting due to WiFi not connecting...");
      ESP.restart();
    }
  }
  Serial.println("Connected to WiFi");
}

void loop() {
  delay(1500);
  int ppm = analogRead(36);
  float temp = dht.getTemperature();
  float hum = dht.getHumidity();
  float tempF = temp * 9.0 / 5.0 + 32.0; // Convert to Fahrenheit
  Serial.print(ppm);
  Serial.print(",");
  Serial.print(" Temperature = ");
  Serial.print(tempF);
  Serial.print("F, ");
  Serial.print("Humidity = ");
  Serial.print(hum);
  Serial.println("%");
  String danger;
  if (ppm <= 1200)
  {
    danger = "Low";
    analogWrite(RED, 0);
    analogWrite(GREEN, 255);
    analogWrite(BLUE, 0);
    myservo.tone(4, 0, 0); // Piezo
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("Temp: ");
    lcd.print(tempF);
    lcd.print("F");
    lcd.setCursor(0, 1);
    lcd.print("Hum: ");
    lcd.print(hum);
    lcd.print("%");
  }
  else if (ppm > 1200 && ppm <= 2000)
{
  danger = "Med";
  analogWrite(RED, 0);
  analogWrite(GREEN, 0);
  analogWrite(BLUE, 255);
  for(int i = 0; i < 5; i++) // Beep 5 times
  {
    myservo.tone(4, 5000);
    delay(1000); // Beep for 1000ms
    myservo.tone(4, 0); // Silence
    delay(500); // Silence for 500ms
  }
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("Medium Danger:");
  lcd.setCursor(0, 1);
  lcd.print(ppm);
  lcd.print("ppm");
}
  else 
  {
    danger = "High";
    analogWrite(RED, 255);
    analogWrite(GREEN, 0);
    analogWrite(BLUE, 0);
    myservo.tone(4, 5000);
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("High Danger:");
    lcd.setCursor(0, 1);
    lcd.print(ppm);
    lcd.print("ppm");
  }
  postRequest(ppm, temp, hum, danger);
}