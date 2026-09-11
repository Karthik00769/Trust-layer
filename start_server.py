import subprocess
import sys
import os

CREATE_NEW_PROCESS_GROUP = 0x00000200
DETACHED_PROCESS = 0x00000008

env = os.environ.copy()
venv_path = r'C:\Users\Karthik\Downloads\trust-layer\backend\venv'
env['PATH'] = f"{venv_path}\\Scripts;" + env['PATH']
env['VIRTUAL_ENV'] = venv_path

p = subprocess.Popen(
    [sys.executable, "-m", "uvicorn", "app.main:app", "--port", "8000", "--host", "0.0.0.0"],
    cwd=r"C:\Users\Karthik\Downloads\trust-layer\backend",
    env=env,
    creationflags=DETACHED_PROCESS | CREATE_NEW_PROCESS_GROUP
)
print(f"Started uvicorn with PID {p.pid}")
