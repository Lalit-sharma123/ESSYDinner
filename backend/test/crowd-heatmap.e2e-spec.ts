import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';

describe('Crowd Heat Map & Analytics API (e2e)', () => {
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

  it('/api/v1/analytics/crowd/traffic (POST) - Record hourly traffic data', () => {
    return request(app.getHttpServer())
      .post('/api/v1/analytics/crowd/traffic')
      .set('Authorization', `Bearer ${jwtToken}`)
      .send({
        restaurantId: 'rest_1',
        dayOfWeek: 5,
        hourOfDay: 19,
        walkInCount: 25,
        bookingCount: 55,
      })
      .expect(201)
      .expect((res) => {
        expect(res.body.occupancyRate).toBeGreaterThan(0);
        expect(res.body.crowdLevel).toBe('Busy');
      });
  });

  it('/api/v1/analytics/crowd/heatmap/rest_1 (GET) - Fetch 24-hr heatmap', () => {
    return request(app.getHttpServer())
      .get('/api/v1/analytics/crowd/heatmap/rest_1?dayOfWeek=5')
      .set('Authorization', `Bearer ${jwtToken}`)
      .expect(200)
      .expect((res) => {
        expect(Array.isArray(res.body)).toBe(true);
      });
  });

  it('/api/v1/analytics/crowd/weekly-predictions/rest_1 (GET) - Fetch weekly predictions', () => {
    return request(app.getHttpServer())
      .get('/api/v1/analytics/crowd/weekly-predictions/rest_1')
      .set('Authorization', `Bearer ${jwtToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.weeklyPredictions).toBeDefined();
        expect(res.body.peakHours).toBeDefined();
      });
  });
});
