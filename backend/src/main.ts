import 'dotenv/config';
import { NestFactory } from '@nestjs/core';
import { ValidationPipe } from '@nestjs/common';
import { SwaggerModule, DocumentBuilder } from '@nestjs/swagger';
import { AppModule } from './app.module';
import helmet from 'helmet';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);

  // Security Middleware
  app.use(helmet());
  app.enableCors({ origin: '*' });

  // Global Validation
  app.useGlobalPipes(
    new ValidationPipe({
      whitelist: true,
      transform: true,
      forbidNonWhitelisted: true,
    }),
  );

  // Swagger Documentation Setup
  const config = new DocumentBuilder()
    .setTitle('DineReserve Backend Microservices API')
    .setDescription(
      'Production-grade RESTful & Real-time WebSocket API powering the DineReserve Android Platform.',
    )
    .setVersion('1.0.0')
    .addBearerAuth()
    .build();

  const document = SwaggerModule.createDocument(app, config);
  SwaggerModule.setup('swagger', app, document);

  const preferredPort = parseInt(process.env.BACKEND_PORT || '3000', 10);
  let port = preferredPort;

  try {
    await app.listen(port);
  } catch (err: any) {
    if (err?.code === 'EADDRINUSE') {
      port = preferredPort + 1;
      console.warn(`Port ${preferredPort} in use, trying fallback port ${port}`);
      await app.listen(port);
    } else {
      throw err;
    }
  }

  console.log(`🚀 DineReserve Backend running on port ${port}`);
  console.log(`📑 Swagger Documentation available at http://localhost:${port}/swagger`);
}

bootstrap();
