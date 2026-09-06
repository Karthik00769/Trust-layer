from fastapi import FastAPI

from app.api.routes import router


app = FastAPI(
    title="Buddy — AI Financial Firewall API",
    description="Backend API for Buddy — AI Financial Firewall",
    version="0.1.0"
)


app.include_router(router)


@app.get("/")
def root():
    return {
        "message": "Buddy Financial Firewall API is running",
        "version": "0.1.0"
    }