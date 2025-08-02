# PulseWatch - PPG Signal Processing Backend
bacckend system for processing Photoplethysmography (PPG) signals captured from smartphone cameras or wearables. The backend handles signal preprocessing, health metric extraction, and integrates with message brokers for mobile applications.

## Features

- **Real-time PPG Signal Processing**: Process PPG signals from smartphone cameras or wearables
- **Health Monitoring**: Monitor heart rate (BPM), blood oxygen levels (SpO₂), heart rate variability (HRV), and detect potential arrhythmias
- **AI/ML Anomaly Detection**: Leverage AI/ML to detect anomalies and provide early warning alerts
- **Low-Resource Optimization**: Optimized algorithms for low-resource settings (offline support, low-bandwidth friendly)
- **Gamification**: Streak tracking, achievements, virtual coach, and health scoring
- **Asynchronous Processing**: RabbitMQ message broker for real-time updates
- **Mobile Integration**: Clean DTOs optimized for React Native frontend
- **Cross-platform Support**: Works seamlessly across devices
- **RESTful API**: Clean, well-documented API endpoints
- **Message Broker Integration**: RabbitMQ for real-time communication for real-time processing
- **DTO Architecture**: Clean separation of concerns with request/response DTOs
- **Validation**: Comprehensive input validation and error handling

### API Endpoints (v1)
- **POST /api/v1/ppg/analyze**: Analyze single PPG signal
- **POST /api/v1/ppg/realtime**: Real-time PPG analysis
- **POST /api/v1/ppg/batch**: Batch analysis of multiple signals
- **GET /api/v1/ppg/health/{userId}**: User health summary
- **GET /api/v1/ppg/status**: System status and health
- **POST /api/v1/ppg/demo**: Demo analysis with synthetic data
- **GET /api/v1/ppg/health-summary**: Get health summary for a user
- **GET /api/v1/ppg/gamification/{userId}**: Get gamification data including streaks, achievements, and leaderboard position

## Architecture

### Backend Design
- **Spring Boot 3.5.4**: REST API framework
- **Apache Commons Math**: Statistical and signal processing utilities
- **RabbitMQ**: Message broker for asynchronous processing
- **Maven**: Build and dependency management
- **Java 21**: Modern Java features

### Mobile Integration Architecture
```
Mobile App → REST API → Signal Processing → Health Analysis → Message Broker → Response
```

