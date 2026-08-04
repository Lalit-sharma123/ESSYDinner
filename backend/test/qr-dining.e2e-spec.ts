import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';

describe('QR Dining & Split Bill API (e2e)', () => {
  let app: INestApplication;
  let jwtToken: string;
  let sessionId: string;
  let shareId: string;

  beforeAll(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    app = moduleFixture.createNestApplication();
    app.useGlobalPipes(new ValidationPipe({ whitelist: true }));
    await app.init();

    // Authenticate test user
    const res = await request(app.getHttpServer())
      .post('/api/v1/auth/login')
      .send({ email: 'john.doe@example.com', password: 'Password123!' });
    jwtToken = res.body.accessToken;
  });

  afterAll(async () => {
    await app.close();
  });

  it('/api/v1/qr-dining/session (POST) - Start Dining Session', () => {
    return request(app.getHttpServer())
      .post('/api/v1/qr-dining/session')
      .set('Authorization', `Bearer ${jwtToken}`)
      .send({
        restaurantId: 'rest_1',
        tableNumber: 'Table 14',
      })
      .expect(201)
      .expect((res) => {
        expect(res.body).toHaveProperty('id');
        expect(res.body.tableNumber).toBe('Table 14');
        sessionId = res.body.id;
      });
  });

  it('/api/v1/qr-dining/session/:id/order (POST) - Add Order Items', async () => {
    const res = await request(app.getHttpServer())
      .post(`/api/v1/qr-dining/session/${sessionId}/order`)
      .set('Authorization', `Bearer ${jwtToken}`)
      .send({
        menuItemId: 'mi_1',
        itemName: 'Truffle Tagliatelle',
        unitPrice: 34.0,
        quantity: 2,
        notes: 'Extra parmesan',
      })
      .expect(201);

    expect(res.body).toHaveProperty('id');
  });

  it('/api/v1/qr-dining/session/:id/split (POST) - Equal Split Bill', () => {
    return request(app.getHttpServer())
      .post(`/api/v1/qr-dining/session/${sessionId}/split`)
      .set('Authorization', `Bearer ${jwtToken}`)
      .send({
        splitType: 'EQUAL',
        numAttendees: 2,
        tipPercent: 18.0,
      })
      .expect(201)
      .expect((res) => {
        expect(res.body).toHaveProperty('id');
        expect(res.body.splitType).toBe('EQUAL');
        expect(res.body.shares).toHaveLength(2);
        shareId = res.body.shares[0].id;
      });
  });

  it('/api/v1/qr-dining/share/:shareId/pay (POST) - Pay Share', () => {
    return request(app.getHttpServer())
      .post(`/api/v1/qr-dining/share/${shareId}/pay`)
      .set('Authorization', `Bearer ${jwtToken}`)
      .expect(201)
      .expect((res) => {
        expect(res.body.updatedShare.isPaid).toBe(true);
      });
  });
});
