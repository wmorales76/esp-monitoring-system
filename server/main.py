from flask import Flask, request, jsonify
from dbmodels import *
from werkzeug.security import generate_password_hash, check_password_hash
from sqlalchemy.exc import IntegrityError

app = Flask(__name__)

app.config["SQLALCHEMY_DATABASE_URI"] = "mysql://root:wmorales@localhost/capstone"
app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False  # Optional: suppresses a warning

db.init_app(app)

with app.app_context():
    db.create_all()


@app.route("/signup", methods=["POST"])
def signup():
    data = request.get_json()
    hashed_password = generate_password_hash(data["password"], method="pbkdf2:sha256")
    new_user = Users(
        Username=data["username"],
        Hash=hashed_password,
        Name=data["name"],
        Email=data["email"],
    )

    db.session.add(new_user)
    db.session.commit()
    return jsonify({"message": "registered successfully"}), 201


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
    new_info = DeviceInformation(
        Building=data["building"],
        Room=data["room"],
        Area=data["area"],
        Purpose=data["purpose"],
    )
    db.session.add(new_info)
    db.session.commit()
    return (
        jsonify(
            {"message": "device info added successfully", "info_id": new_info.Info_ID}
        ),
        201,
    )


@app.route("/add_device", methods=["POST"])
def add_device():
    data = request.get_json()
    user = Users.query.filter_by(Username=data["username"]).first()
    try:
        new_device = Devices(
            Device_ID=data["device_id"],
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
    data = request.get_json()
    user = Users.query.filter_by(Username=data["username"]).first()
    devices = Devices.query.filter_by(User_ID=user.User_ID).all()
    device_list = []
    for device in devices:
        device_list.append(device.Device_ID)
    return jsonify({"devices": device_list}), 200


@app.route("/retrieve_device_info", methods=["POST"])
def retrieve_device_info():
    data = request.get_json()
    device = Devices.query.filter_by(Device_ID=data["device_id"]).first()
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


@app.route("/device_monitor", methods=["POST"])
def add_data():
    content = request.json
    ppm = content.get("ppm")
    id = content.get("device_id")
    new_data = DeviceMonitor(
        Parts_Per_Million=ppm,
        Temperature="0",
        Relative_Humidity="0",
        Device_ID=id,
        Danger="0",
    )

    try:
        db.session.add(new_data)
        db.session.commit()
        return jsonify({"message": "success"})
    except Exception as e:
        db.session.rollback()
        return jsonify({"message": "failed" + str(e)}), 500


@app.route("/device_monitor/<device_id>", methods=["GET"])
def get_data(device_id):
    try:
        # import logging
        # logging.basicConfig()
        # logging.getLogger('sqlalchemy.engine').setLevel(logging.INFO)
        latest_data = (
            DeviceMonitor.query.filter_by(Device_ID=device_id)
            .order_by(DeviceMonitor.Timestamp.desc())
            .first()
        )
        return jsonify(
            {
                "ppm": latest_data.Parts_Per_Million,
                "temp": latest_data.Temperature,
                "hum": latest_data.Relative_Humidity,
                "danger": latest_data.Danger,
            }
        )
    except Exception as e:
        return jsonify({"message": "failed" + str(e)}), 500


if __name__ == "__main__":
    app.run(
        host="0.0.0.0",
        port=5000,
        debug=True,
    )
