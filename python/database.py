import pymysql

# DB 접속
def get_connection():
    return pymysql.connect(
        host="localhost",
        user="root",
        password="12345",
        database="edudb",
        charset="utf8mb4"
    )