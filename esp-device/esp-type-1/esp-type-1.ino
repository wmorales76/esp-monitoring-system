#include <Servo.h>
#include <pwmWrite.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>
#include "LiquidCrystal_I2C.h"
#include <DHTesp.h>

#define RED 13
#define GREEN 27
#define BLUE 32
#define MQ_SENSOR_PIN 36
#define PIEZO_PIN 4
#define DHT_PIN 15

#define Device_UID "room_1_wmorales"
#define server "http://192.168.131.89:5000"
#define ssid "Galaxy1"
#define password "rakudai21"

LiquidCrystal_I2C lcd(0x27,16,2);
DHTesp dht;
Servo myservo;

void sendToServer(int ppm, float temp, float hum, String danger) {
  HTTPClient http;
  String url = String(server) + "/device_monitor";

  DynamicJsonDocument doc(256);
  doc["ppm"] = String(ppm);
  doc["temp"] = String(temp);
  doc["hum"] = String(hum);
  doc["danger"] = danger;
  doc["device_uid"] = Device_UID;

  String payload;
  serializeJson(doc, payload);

  http.begin(url);
  http.addHeader("Content-Type", "application/json");
  int httpResponseCode = http.POST(payload);

  if (httpResponseCode > 0) {
    Serial.print("HTTP Response code: ");
    Serial.println(httpResponseCode);
    String response = http.getString();
    Serial.println(response);
  }
  else {
    Serial.print("Error code: ");
    Serial.println(httpResponseCode);
  }
  http.end();
}

void setup() {
  Serial.begin(9600);
  lcd.init();
  lcd.backlight();
  lcd.print("Initializing! :)");
  dht.setup(DHT_PIN, DHTesp::DHT11);
  
  pinMode(MQ_SENSOR_PIN, OUTPUT);
  pinMode(PIEZO_PIN, OUTPUT);
  pinMode(RED, OUTPUT);
  pinMode(GREEN, OUTPUT);
  pinMode(BLUE, OUTPUT);

  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.println("Connecting to WiFi...");
  }
  Serial.println("Connected to WiFi");
}

void loop() {
  delay(1500);
  int ppm = analogRead(MQ_SENSOR_PIN);
  float temp = dht.getTemperature();
  float hum = dht.getHumidity();
  float tempF = temp * 9.0 / 5.0 + 32.0; // Convert to Fahrenheit

  Serial.print("PPM: ");
  Serial.print(ppm);
  Serial.print(", Temperature: ");
  Serial.print(tempF);
  Serial.print("F, Humidity: ");
  Serial.print(hum);
  Serial.println("%");

  String danger;
  if (ppm <= 1200) {
    danger = "Low";
    analogWrite(RED, 0);
    analogWrite(GREEN, 255);
    analogWrite(BLUE, 0);
    myservo.tone(PIEZO_PIN, 0, 0);
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
  else if (ppm > 1200 && ppm <= 2000) {
    danger = "High";
    analogWrite(RED, 0);
    analogWrite(GREEN, 255);
    analogWrite(BLUE, 255);
    for(int i = 0; i < 5; i++) {
      myservo.tone(PIEZO_PIN, 5000);
      myservo.tone(PIEZO_PIN, 0);
      delay(500);
    }
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("Medium Danger:");
    lcd.setCursor(0, 1);
    lcd.print(ppm);
    lcd.print("ppm");
  }
  else {
    danger = "High";
    analogWrite(RED, 0);
    analogWrite(GREEN, 0);
    analogWrite(BLUE, 255);
    myservo.tone(PIEZO_PIN, 5000);
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("High Danger:");
    lcd.setCursor(0, 1);
    lcd.print(ppm);
    lcd.print("ppm");
  }
  sendToServer(ppm, temp, hum, danger);
}