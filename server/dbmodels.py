from flask_sqlalchemy import SQLAlchemy

db = SQLAlchemy()

class Users(db.Model):
    User_ID = db.Column(db.Integer, primary_key=True, autoincrement=True)
    Username = db.Column(db.String(50), nullable=True)
    Hash = db.Column(db.Text, nullable=True)
    Name = db.Column(db.Text, nullable=True)
    Phone = db.Column(db.Text, nullable=True)
    Email = db.Column(db.Text, nullable=True)
    Role = db.Column(db.Text, nullable=True)
    Timestamp = db.Column(db.DateTime, default=db.func.current_timestamp())

class Devices(db.Model):
    Device_ID = db.Column(db.Integer, primary_key=True)
    Device_Name = db.Column(db.String(45), nullable=True)
    User_ID = db.Column(db.Integer, db.ForeignKey('users.User_ID'), nullable=False)
    Device_UID = db.Column(db.String(45), nullable=True)

class DeviceMonitor(db.Model):
    Monitor_ID = db.Column(db.Integer, primary_key=True, autoincrement=True)
    Parts_Per_Million = db.Column(db.String(255), nullable=True)
    Temperature = db.Column(db.String(255), nullable=True)
    Relative_Humidity = db.Column(db.String(255), nullable=True)
    Danger = db.Column(db.String(255), nullable=True)
    Timestamp = db.Column(db.DateTime, default=db.func.current_timestamp())
    Device_ID = db.Column(db.String(255), db.ForeignKey('devices.Device_ID'), nullable=False)

class DeviceInformation(db.Model):
    Info_ID = db.Column(db.Integer, primary_key=True, autoincrement=True)
    Building = db.Column(db.Text, nullable=True)
    Room = db.Column(db.Text, nullable=True)
    Area = db.Column(db.Text, nullable=True)
    Purpose = db.Column(db.Text, nullable=True)
    Device_ID = db.Column(db.String(255), db.ForeignKey('devices.Device_ID'), nullable=False)