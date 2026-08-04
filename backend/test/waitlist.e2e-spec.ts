import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';

describe('Waitlist Queue API (e2e)', () => {
  let app: INestApplication;
  let jwtToken: string;
  let waitlistEntryId: string;

  beforeAll(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    app = moduleFixture.createNestApplication();
    app.useGlobalPipes(new ValidationPipe({ whitelist: true }));
    await app.init();

    // Authenticate test customer
    const res = await request(app.getHttpServer())
      .post('/api/v1/auth/login')
      .send({ email: 'john.doe@example.com', password: 'Password123!' });
    jwtToken = res.body.accessToken;
  });

  afterAll(async () => {
    await app.close();
  });

  it('/api/v1/waitlist (POST) - Join Waitlist Queue', () => {
    return request(app.getHttpServer())
      .post('/api/v1/waitlist')
      .set('Authorization', `Bearer ${jwtToken}`)
      .send({
        restaurantId: 'rest_1',
        partySize: 2,
        isPriority: true,
      })
      .expect(201)
      .expect((res) => {
        expect(res.body).toHaveProperty('id');
        expect(res.body.partySize).toBe(2);
        expect(res.body.isPriority).toBe(true);
        expect(res.body.status).toBe('QUEUED');
        waitlistEntryId = res.body.id;
      });
  });

  it('/api/v1/waitlist/my-waitlist (GET) - Retrieve User Waitlists', () => {
    return request(app.getHttpServer())
      .get('/api/v1/waitlist/my-waitlist')
      .set('Authorization', `Bearer ${jwtToken}`)
      .expect(200)
      .expect((res) => {
        expect(Array.isArray(res.body)).toBe(true);
        expect(res.body.length).toBeGreaterThan(0);
      });
  });

  it('/api/v1/waitlist/:id/leave (PATCH) - Cancel/Leave Waitlist Queue', () => {
    return request(app.getHttpServer())
      .patch(`/api/v1/waitlist/${waitlistEntryId}/leave`)
      .set('Authorization', `Bearer ${jwtToken}`)
      .expect(200)
      .expect((res) => {
        expect(res.body.status).toBe('CANCELLED');
      });
  });
});
