import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';

describe('Live Parking Availability Service (e2e)', () => {
  let app: INestApplication;
  let jwtToken: string;
  let lotId: string;
  let slotId: string;

  beforeAll(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    app = moduleFixture.createNestApplication();
    app.useGlobalPipes(new ValidationPipe({ whitelist: true }));
    await app.init();

    const loginRes = await request(app.getHttpServer())
      .post('/api/v1/auth/login')
      .send({ email: 'john.doe@example.com', password: 'Password123!' });
    jwtToken = loginRes.body.accessToken;
  });

  afterAll(async () => {
    await app.close();
  });

  it('/api/v1/parking/restaurant/rest_1 (GET) - Fetch live parking lots and slots', () => {
    return request(app.getHttpServer())
      .get('/api/v1/parking/restaurant/rest_1')
      .set('Authorization', `Bearer ${jwtToken}`)
      .expect(200)
      .expect((res) => {
        expect(Array.isArray(res.body)).toBe(true);
        expect(res.body.length).toBeGreaterThan(0);
        lotId = res.body[0].id;
        slotId = res.body[0].slots[0].id;
      });
  });

  it('/api/v1/parking/slots/:id/state (PATCH) - Update parking slot state to Occupied', () => {
    return request(app.getHttpServer())
      .patch(`/api/v1/parking/slots/${slotId}/state`)
      .set('Authorization', `Bearer ${jwtToken}`)
      .send({
        state: 'Occupied',
        occupiedBy: 'Guest VIP Porsche',
      })
      .expect(200)
      .expect((res) => {
        expect(res.body.state).toBe('Occupied');
        expect(res.body.occupiedBy).toBe('Guest VIP Porsche');
      });
  });
});
