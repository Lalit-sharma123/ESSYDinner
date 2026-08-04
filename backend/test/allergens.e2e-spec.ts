import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';

describe('Allergy Protection Engine (e2e)', () => {
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

  it('/api/v1/allergens/taxonomy (GET) - Fetch 14 standard allergens', () => {
    return request(app.getHttpServer())
      .get('/api/v1/allergens/taxonomy')
      .set('Authorization', `Bearer ${jwtToken}`)
      .expect(200)
      .expect((res) => {
        expect(Array.isArray(res.body)).toBe(true);
        expect(res.body.length).toBe(14);
      });
  });

  it('/api/v1/allergens/profile (PUT) - Update user health allergy profiles', () => {
    return request(app.getHttpServer())
      .put('/api/v1/allergens/profile')
      .set('Authorization', `Bearer ${jwtToken}`)
      .send({
        allergens: ['PEANUTS', 'DAIRY', 'GLUTEN'],
      })
      .expect(200)
      .expect((res) => {
        expect(Array.isArray(res.body)).toBe(true);
        expect(res.body.length).toBe(3);
      });
  });

  it('/api/v1/allergens/check-order (POST) - Validate order items against health allergies', () => {
    return request(app.getHttpServer())
      .post('/api/v1/allergens/check-order')
      .set('Authorization', `Bearer ${jwtToken}`)
      .send({
        menuItemIds: ['mi_1'],
      })
      .expect(201)
      .expect((res) => {
        expect(res.body).toHaveProperty('safe');
        expect(res.body).toHaveProperty('warnings');
      });
  });
});
