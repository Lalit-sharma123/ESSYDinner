import { Body, Controller, Post, HttpCode, HttpStatus } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiResponse } from '@nestjs/swagger';
import { AuthService } from './auth.service';
import { LoginDto, RegisterDto, PhoneOtpDto } from './dto/auth.dto';

@ApiTags('Authentication')
@Controller('api/v1/auth')
export class AuthController {
  constructor(private authService: AuthService) {}

  @Post('register')
  @ApiOperation({ summary: 'Register new customer or owner' })
  @ApiResponse({ status: 201, description: 'User successfully created' })
  async register(@Body() dto: RegisterDto) {
    return this.authService.register(dto);
  }

  @Post('login')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'JWT User Login' })
  @ApiResponse({ status: 200, description: 'Authenticated with Bearer Token' })
  async login(@Body() dto: LoginDto) {
    return this.authService.login(dto);
  }

  @Post('phone-otp')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Verify SMS Phone OTP' })
  async verifyPhoneOtp(@Body() dto: PhoneOtpDto) {
    return this.authService.verifyPhoneOtp(dto);
  }
}
