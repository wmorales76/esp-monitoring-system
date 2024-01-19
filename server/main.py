from flask import Flask, request, jsonify
from dbmodels import *
from werkzeug.security import generate_password_hash, check_password_hash
from sqlalchemy.exc import IntegrityError
from werkzeug.security import generate_password_hash, check_password_hash

app = Flask(__name__)

app.config["SQLALCHEMY_DATABASE_URI"] = "mysql://admin:wmorales@capstone-database.cbgkiweqag9q.us-east-2.rds.amazonaws.com:3306/capstone"

app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False  # Optional: suppresses a warning
db.init_app(app)

with app.app_context():
    db.create_all()

@app.route("/signup", methods=["POST"])
def signup():
    data = request.get_json()
    hashed_password = generate_password_hash(data["password"], method="sha256")
    new_user = Users(
        Username=data["username"],
        Hash=hashed_password,
        Name=data["name"],
        Email=data["email"],

    )

    if not Users.query.filter_by(Username=data["username"]).first():
        db.session.add(new_user)
        db.session.commit()
        return jsonify({"message": "registered successfully"}), 201
    else:
        return jsonify({"message": "username already exists"}), 409

@app.route("/login", methods=["POST"])
def login():
    data = request.get_json()
    user = Users.query.filter_by(Username=data["username"]).first()
    if not user or not check_password_hash(user.Hash, data["password"]):
        return jsonify({"message": "login failed"}), 401
    return jsonify({"message": "logged in successfully"}), 200

@app.route("/add_device_info", methods=["POST"])
def add_device_info():
    data = request.get_json()
    device = Devices.query.filter_by(Device_UID=data["device_uid"]).first()
    if device is None:
        return jsonify({"message": "device not found"}), 404

    new_info = DeviceInformation(
        Building=data["building"],
        Room=data["room"],
        Area=data["area"],
        Purpose=data["purpose"],
        Device_ID=device.Device_ID  # Corrected this line
    )
    db.session.add(new_info)
    db.session.commit()
    return (
        jsonify({"message": "device info added successfully", "info_id": new_info.Info_ID}),
        201,
    )

@app.route("/add_device", methods=["POST"])
def add_device():
    data = request.get_json()
    user = Users.query.filter_by(Username=data["username"]).first()
    try:
        new_device = Devices(
            Device_UID=data["device_uid"],
            Device_Name=data["device_name"],
            User_ID=user.User_ID,
        )
        db.session.add(new_device)
        db.session.commit()
        return jsonify({"message": "device added successfully"}), 201
    except IntegrityError:
        db.session.rollback()
        return jsonify({"message": "duplicate entry"}), 409

@app.route("/retrieve_devices", methods=["POST"])
def retrieve_devices():
    #import logging
    #logging.basicConfig()
    #logging.getLogger('sqlalchemy.engine').setLevel(logging.INFO)
    data = request.get_json()
    user = Users.query.filter_by(Username=data["username"]).first()
    devices = Devices.query.filter_by(User_ID=user.User_ID).all()
    device_list = []
    for device in devices:
        device_list.append(device.Device_UID)
    return jsonify({"devices": device_list}), 200

@app.route("/retrieve_device_info", methods=["POST"])
def retrieve_device_info():
    data = request.get_json()
    device = Devices.query.filter_by(Device_UID=data["device_uid"]).first()
    info = DeviceInformation.query.filter_by(Info_ID=device.Info_ID).first()
    return (
        jsonify(
            {
                "building": info.Building,
                "room": info.Room,
                "area": info.Area,
                "purpose": info.Purpose,
            }
        ),
        200,
    )
@app.route("/retrieve_monitoring_data", methods=["POST"])
def retrieve_monitoring_data():
    data = request.get_json()
    device = Devices.query.filter_by(Device_UID=data["device_uid"]).first()
    if device is None:
        return jsonify({"message": "device not found"}), 404

    monitoring_data = DeviceMonitor.query.filter_by(Device_ID=device.Device_ID).all()
    data_list = []
    for data in monitoring_data:
        data_list.append({
            "ppm": data.Parts_Per_Million,
            "temp": data.Temperature,
            "hum": data.Relative_Humidity,
            "danger": data.Danger
        })
    return jsonify({"monitoring_data": data_list}), 200

@app.route("/device_monitor", methods=["POST"])
def add_data():
    data = request.json
    device = Devices.query.filter_by(Device_UID=data["device_uid"]).first()
    if device is None:
        return jsonify({"message": "device not found"}), 404

    new_data = DeviceMonitor(
        Parts_Per_Million=data["ppm"],
        Temperature=data["temp"],
        Relative_Humidity=data["hum"],
        Device_ID=device.Device_ID,
        Danger=data["danger"],
    )
    try:
        db.session.add(new_data)
        db.session.commit()
        return jsonify({"message": "success"})
    except Exception as e:
        db.session.rollback()
        return jsonify({"message": "failed" + str(e)}), 500

@app.route("/read_monitor/", methods=["POST"])
def get_data():
    data = request.json
    device_uid = data["device_uid"]
    try:
        device = Devices.query.filter_by(Device_UID=device_uid).first()
        if device is None:
            return jsonify({"message": "Device not found."}), 404

        latest_data = (
            DeviceMonitor.query.filter_by(Device_ID=device.Device_ID)
            .order_by(DeviceMonitor.Timestamp.desc())
            .first()
        )
        if latest_data is not None:
            return jsonify(
                {
                    "ppm": latest_data.Parts_Per_Million,
                    "temp": latest_data.Temperature,
                    "hum": latest_data.Relative_Humidity,
                    "danger": latest_data.Danger,
                }
            )
        else:
            
            return jsonify({"message":"No data available for the specified device."}), 404
    except Exception as e:
        return jsonify({"message": "Failed: " + str(e)}), 500


if __name__ == "__main__":
    app.run(
        host="0.0.0.0",
        port=5000,
        debug=True,
    )
