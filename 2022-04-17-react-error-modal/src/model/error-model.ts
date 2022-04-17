export interface ErrorModel {
    code: string, // USER_NOT_FOUND, INVALID_LOGIN_FORM 등, 오류의 식별자
    message: string // 오류 메시지
}