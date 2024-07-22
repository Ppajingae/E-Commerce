package com.highv.ecommerce.login

import com.highv.ecommerce.domain.auth.dto.LoginRequest
import com.highv.ecommerce.domain.auth.service.UserService
import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.seller.entity.Seller
import com.highv.ecommerce.domain.seller.repository.SellerRepository
import com.highv.ecommerce.infra.email.EmailUtils
import com.highv.ecommerce.infra.redis.RedisUtils
import com.highv.ecommerce.infra.security.jwt.JwtPlugin
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.security.crypto.password.PasswordEncoder

class loginUserServiceTest {

    private val buyerRepository: BuyerRepository = mock(BuyerRepository::class.java)
    private val sellerRepository: SellerRepository = mock(SellerRepository::class.java)
    private val passwordEncoder: PasswordEncoder = mock(PasswordEncoder::class.java)
    private val jwtPlugin: JwtPlugin = mock(JwtPlugin::class.java)
    private val redisUtils = mockk<RedisUtils>()
    private val emailUtils = mockk<EmailUtils>()
    private val userService: UserService =
        UserService(buyerRepository, sellerRepository, passwordEncoder, jwtPlugin, redisUtils, emailUtils, 5000)

    @Test
    fun `구매자 로그인 성공 테스트`() {
        // given
        val loginRequest = LoginRequest(email = "buyer@example.com", password = "password123")
        val buyer = Buyer(
            id = 1L,
            email = "buyer@example.com",
            password = "encodedPassword",
            nickname = "buyer",
            profileImage = "profile.png",
            phoneNumber = "010-1234-5678",
            address = "Seoul, Korea",
            providerName = null,
            providerId = null
        )

        `when`(buyerRepository.findByEmail(loginRequest.email)).thenReturn(buyer)
        `when`(passwordEncoder.matches(loginRequest.password, buyer.password)).thenReturn(true)
        `when`(jwtPlugin.generateAccessToken(buyer.id.toString(), buyer.email, "BUYER")).thenReturn("token")

        // when
        val response = userService.loginBuyer(loginRequest)

        // then
        assertEquals("token", response.accessToken)
    }

    @Test
    fun `판매자 로그인 성공 테스트`() {
        // given
        val loginRequest = LoginRequest(email = "seller@example.com", password = "password123")
        val seller = Seller(
            id = 1L,
            email = "seller@example.com",
            password = "encodedPassword",
            nickname = "seller",
            profileImage = "profile.png",
            phoneNumber = "010-1234-5678",
            address = "Seoul, Korea"
        )

        `when`(sellerRepository.findByEmail(loginRequest.email)).thenReturn(seller)
        `when`(passwordEncoder.matches(loginRequest.password, seller.password)).thenReturn(true)
        `when`(jwtPlugin.generateAccessToken(seller.id.toString(), seller.email, "SELLER")).thenReturn("token")

        // when
        val response = userService.loginSeller(loginRequest)

        // then
        assertEquals("token", response.accessToken)
    }

    @Test
    fun `로그인 실패 테스트 - 잘못된 이메일 또는 비밀번호`() {
        // given
        val loginRequest = LoginRequest(email = "nonexistent@example.com", password = "wrongpassword")

        `when`(buyerRepository.findByEmail(loginRequest.email)).thenReturn(null)

        // when & then
        val exception = assertThrows<IllegalArgumentException> { userService.loginBuyer(loginRequest) }
        assertEquals("Invalid email or password", exception.message)
    }

    // 아래 테스트는 함수를 변경해서 테스트하기 좀 애매한 것 같음
    // 일단 나중에 리팩토링때 건들 예정임으로 일단 주석처리

    // @Test
    // fun `로그인 실패 테스트 - 역할 누락`() {
    //     // given
    //     val loginRequest = LoginRequest(email = "buyer@example.com", password = "password123", role = "UNKNOWN")
    //
    //     // when & then
    //     val exception = assertThrows<IllegalArgumentException> { userService.login(loginRequest) }
    //     assertEquals("Invalid email or password", exception.message)
    // }
}