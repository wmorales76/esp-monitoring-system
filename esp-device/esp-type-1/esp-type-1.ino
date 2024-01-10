#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>

#define gasSensor 36
#define deviceID "Device001"
#define server "http://192.168.0.243:5000"
#define ssid "VPNet5183"
#define password "12345678"  // Corrected the variable name from 'pass' to 'password'

void postRequest(int value) {
  HTTPClient client;
  String endpoint = String(server) + "/" + String(deviceID);

  DynamicJsonDocument jsonDocument(256);
  jsonDocument["ppm"] = value;
  jsonDocument["device_id"] = deviceID;
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

void setup() {
  pinMode(gasSensor, INPUT);
  Serial.begin(115200);

  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Connecting to WiFi...");
  }
  Serial.println("Connected to WiFi");
}

void loop() {
  int sensorReading = analogRead(gasSensor);
  postRequest(sensorReading);
  delay(10000);
}
