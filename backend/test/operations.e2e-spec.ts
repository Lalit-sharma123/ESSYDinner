import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';

describe('Restaurant Operations Dashboard (e2e)', () => {
  let app: INestApplication;
  let jwtToken: string;

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

  it('/api/v1/operations/dashboard/rest_1 (GET) - Fetch live KPIs and Operational Dashboard metrics', () => {
    return request(app.getHttpServer())
      .get('/api/v1/operations/dashboard/rest_1')
      .set('Authorization', `Bearer ${jwtToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.tables).toBeDefined();
        expect(res.body.kitchen).toBeDefined();
        expect(res.body.todayMetrics).toBeDefined();
        expect(res.body.parking).toBeDefined();
      });
  });
});
