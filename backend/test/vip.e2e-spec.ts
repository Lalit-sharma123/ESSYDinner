import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';

describe('VIP Customer Recognition Engine (e2e)', () => {
  let app: INestApplication;
  let jwtToken: string;
  let userId: string;

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
    userId = loginRes.body.user.id;
  });

  afterAll(async () => {
    await app.close();
  });

  it('/api/v1/vip/profile/:userId (GET) - Fetch VIP profile & visit history', () => {
    return request(app.getHttpServer())
      .get(`/api/v1/vip/profile/${userId}`)
      .set('Authorization', `Bearer ${jwtToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.user).toBeDefined();
        expect(res.body.vipProfile).toBeDefined();
      });
  });

  it('/api/v1/vip/check-in (POST) - Process check-in and recognize VIP customer with staff alert', () => {
    return request(app.getHttpServer())
      .post('/api/v1/vip/check-in')
      .set('Authorization', `Bearer ${jwtToken}`)
      .send({
        userId,
        restaurantId: 'rest_1',
        tableNumber: 'Booth #4',
      })
      .expect(201)
      .expect((res) => {
        expect(res.body.recognized).toBe(true);
        expect(res.body.vipAlert).toBeDefined();
        expect(res.body.vipAlert.customerName).toBeDefined();
      });
  });
});
