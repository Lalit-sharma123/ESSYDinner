import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from '../src/app.module';

describe('Service Requests & Staff Tasks (e2e)', () => {
  let app: INestApplication;
  let createdRequestId: string;
  let createdTaskId: string;

  beforeAll(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    app = moduleFixture.createNestApplication();
    app.useGlobalPipes(new ValidationPipe({ transform: true, whitelist: true }));
    await app.init();
  });

  afterAll(async () => {
    await app.close();
  });

  it('/api/v1/service-requests (POST) - Create Water Request', async () => {
    const response = await request(app.getHttpServer())
      .post('/api/v1/service-requests')
      .send({
        restaurantId: 'rest_1',
        diningSessionId: 'session_test_101',
        tableNumber: 'T-04',
        requestType: 'WATER',
        priority: 'NORMAL',
        notes: 'Cold water please',
        items: [
          {
            itemName: 'Sparkling Water',
            quantity: 2,
            price: 4.5,
            specialInstructions: 'With ice & lemon',
          },
        ],
      })
      .expect(201);

    expect(response.body).toHaveProperty('request');
    expect(response.body.request.requestType).toBe('WATER');
    expect(response.body.request.items).toHaveLength(1);
    createdRequestId = response.body.request.id;
    createdTaskId = response.body.task.id;
  });

  it('/api/v1/service-requests/my (GET) - Get Customer Active Requests', async () => {
    const response = await request(app.getHttpServer())
      .get('/api/v1/service-requests/my?sessionId=session_test_101')
      .expect(200);

    expect(Array.isArray(response.body)).toBe(true);
    expect(response.body.length).toBeGreaterThan(0);
  });

  it('/api/v1/staff/tasks (GET) - Get Staff Tasks Queue', async () => {
    const response = await request(app.getHttpServer())
      .get('/api/v1/staff/tasks?restaurantId=rest_1')
      .expect(200);

    expect(Array.isArray(response.body)).toBe(true);
  });

  it('/api/v1/staff/tasks/:id/accept (PATCH) - Staff Accepts Task', async () => {
    if (!createdTaskId) return;
    const response = await request(app.getHttpServer())
      .patch(`/api/v1/staff/tasks/${createdTaskId}/accept`)
      .expect(200);

    expect(response.body.taskStatus).toBe('ACCEPTED');
  });

  it('/api/v1/staff/tasks/:id/start (PATCH) - Staff Starts Task', async () => {
    if (!createdTaskId) return;
    const response = await request(app.getHttpServer())
      .patch(`/api/v1/staff/tasks/${createdTaskId}/start`)
      .expect(200);

    expect(response.body.taskStatus).toBe('IN_PROGRESS');
  });

  it('/api/v1/staff/tasks/:id/complete (PATCH) - Staff Completes Task', async () => {
    if (!createdTaskId) return;
    const response = await request(app.getHttpServer())
      .patch(`/api/v1/staff/tasks/${createdTaskId}/complete`)
      .expect(200);

    expect(response.body.taskStatus).toBe('COMPLETED');
    expect(response.body.serviceRequest.isResolved).toBe(true);
  });
});
