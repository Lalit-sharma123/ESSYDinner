import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';

describe('Live Table Occupancy Digital Twin (e2e)', () => {
  let app: INestApplication;
  let jwtToken: string;
  let floorId: string;
  let tableId: string;

  beforeAll(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    app = moduleFixture.createNestApplication();
    app.useGlobalPipes(new ValidationPipe({ whitelist: true }));
    await app.init();

    // Authenticate test user
    const loginRes = await request(app.getHttpServer())
      .post('/api/v1/auth/login')
      .send({ email: 'john.doe@example.com', password: 'Password123!' });
    jwtToken = loginRes.body.accessToken;
  });

  afterAll(async () => {
    await app.close();
  });

  it('/api/v1/tables/floors (POST) - Create floor layout', () => {
    return request(app.getHttpServer())
      .post('/api/v1/tables/floors')
      .set('Authorization', `Bearer ${jwtToken}`)
      .send({
        restaurantId: 'rest_1',
        floorName: 'Rooftop Patio',
        level: 2,
      })
      .expect(201)
      .expect((res) => {
        expect(res.body).toHaveProperty('id');
        expect(res.body.floorName).toBe('Rooftop Patio');
        floorId = res.body.id;
      });
  });

  it('/api/v1/tables (POST) - Add digital twin table', () => {
    return request(app.getHttpServer())
      .post('/api/v1/tables')
      .set('Authorization', `Bearer ${jwtToken}`)
      .send({
        restaurantId: 'rest_1',
        floorId,
        tableNumber: 'RT-01',
        capacity: 6,
        positionX: 100.0,
        positionY: 200.0,
        shape: 'RECTANGLE',
      })
      .expect(201)
      .expect((res) => {
        expect(res.body).toHaveProperty('id');
        expect(res.body.tableNumber).toBe('RT-01');
        expect(res.body.status).toBe('AVAILABLE');
        tableId = res.body.id;
      });
  });

  it('/api/v1/tables/:id/status (PATCH) - Transition state to RESERVED then DINING then CLEANING', async () => {
    // 1. AVAILABLE -> RESERVED
    await request(app.getHttpServer())
      .patch(`/api/v1/tables/${tableId}/status`)
      .set('Authorization', `Bearer ${jwtToken}`)
      .send({ status: 'RESERVED', changedBy: 'Host Stand' })
      .expect(200)
      .expect((res) => {
        expect(res.body.status).toBe('RESERVED');
      });

    // 2. RESERVED -> DINING
    await request(app.getHttpServer())
      .patch(`/api/v1/tables/${tableId}/status`)
      .set('Authorization', `Bearer ${jwtToken}`)
      .send({ status: 'DINING', changedBy: 'QR Checkin' })
      .expect(200)
      .expect((res) => {
        expect(res.body.status).toBe('DINING');
      });

    // 3. DINING -> CLEANING
    await request(app.getHttpServer())
      .patch(`/api/v1/tables/${tableId}/status`)
      .set('Authorization', `Bearer ${jwtToken}`)
      .send({ status: 'CLEANING', changedBy: 'Checkout System' })
      .expect(200)
      .expect((res) => {
        expect(res.body.status).toBe('CLEANING');
      });
  });

  it('/api/v1/tables/:id/cleaned (POST) - Complete cleaning & restore status to AVAILABLE', () => {
    return request(app.getHttpServer())
      .post(`/api/v1/tables/${tableId}/cleaned`)
      .set('Authorization', `Bearer ${jwtToken}`)
      .send({ cleanedBy: 'Buster Team Alpha' })
      .expect(201)
      .expect((res) => {
        expect(res.body.status).toBe('AVAILABLE');
      });
  });
});
