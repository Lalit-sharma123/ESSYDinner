import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';

describe('Secure QR Code Generation & Session Service (e2e)', () => {
  let app: INestApplication;
  let jwtToken: string;
  let bookingId: string;
  let encryptedQrToken: string;

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

    // Create a test booking
    const bookingRes = await request(app.getHttpServer())
      .post('/api/v1/bookings')
      .set('Authorization', `Bearer ${jwtToken}`)
      .send({
        restaurantId: 'rest_1',
        partySize: 4,
        bookingDate: '2026-08-10',
        timeSlot: '19:00',
        seatingArea: 'Patio',
      });
    
    bookingId = bookingRes.body.id;
  });

  afterAll(async () => {
    await app.close();
  });

  it('/api/v1/qr-dining/generate-qr (POST) - Generates secure encrypted QR code linked to booking', () => {
    return request(app.getHttpServer())
      .post('/api/v1/qr-dining/generate-qr')
      .set('Authorization', `Bearer ${jwtToken}`)
      .send({
        bookingId,
        tableNumber: 'Table 7',
        ttlMinutes: 60,
      })
      .expect(201)
      .expect((res) => {
        expect(res.body.success).toBe(true);
        expect(res.body.bookingId).toBe(bookingId);
        expect(res.body.tableNumber).toBe('Table 7');
        expect(res.body.encryptedQr).toBeDefined();
        expect(typeof res.body.encryptedQr).toBe('string');
        encryptedQrToken = res.body.encryptedQr;
      });
  });

  it('/api/v1/qr-dining/scan-qr (POST) - Scans and decrypts valid active QR code', () => {
    return request(app.getHttpServer())
      .post('/api/v1/qr-dining/scan-qr')
      .set('Authorization', `Bearer ${jwtToken}`)
      .send({
        encryptedQr: encryptedQrToken,
      })
      .expect(201)
      .expect((res) => {
        expect(res.body.valid).toBe(true);
        expect(res.body.payload.bookingId).toBe(bookingId);
        expect(res.body.payload.tableNumber).toBe('Table 7');
        expect(res.body.session).toBeDefined();
      });
  });

  it('/api/v1/qr-dining/scan-qr (POST) - Rejects tampered / invalid QR string', () => {
    return request(app.getHttpServer())
      .post('/api/v1/qr-dining/scan-qr')
      .set('Authorization', `Bearer ${jwtToken}`)
      .send({
        encryptedQr: 'invalid_tampered_qr_code_payload_123',
      })
      .expect(400);
  });

  it('/api/v1/qr-dining/checkout-qr (POST) - Finalizes checkout and invalidates QR code', () => {
    return request(app.getHttpServer())
      .post('/api/v1/qr-dining/checkout-qr')
      .set('Authorization', `Bearer ${jwtToken}`)
      .send({
        bookingId,
      })
      .expect(201)
      .expect((res) => {
        expect(res.body.success).toBe(true);
      });
  });

  it('/api/v1/qr-dining/scan-qr (POST) - Rejects scanning QR code after checkout (cannot be reused)', () => {
    return request(app.getHttpServer())
      .post('/api/v1/qr-dining/scan-qr')
      .set('Authorization', `Bearer ${jwtToken}`)
      .send({
        encryptedQr: encryptedQrToken,
      })
      .expect(400)
      .expect((res) => {
        expect(res.body.message).toContain('cannot be reused');
      });
  });
});
