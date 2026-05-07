package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.AddMemberByPhoneRequest
import com.karrad.ticketsclient.data.api.dto.AddMemberByPhoneResponse
import com.karrad.ticketsclient.data.api.dto.OrgEventItem
import com.karrad.ticketsclient.data.api.dto.OrgMemberDto
import com.karrad.ticketsclient.data.api.dto.OrgMembershipDto
import com.karrad.ticketsclient.data.api.dto.VenueDto

interface OrgMemberService {
    /** Возвращает членство текущего пользователя в организации, или null если не состоит. */
    suspend fun getMyMembership(): OrgMembershipDto?

    /** Список членов организации (требует OWNER или MANAGER). */
    suspend fun listMembers(): List<OrgMemberDto>

    /** Список площадок организации текущего пользователя. */
    suspend fun listMyVenues(): List<VenueDto>

    /** Список предстоящих мероприятий организации со статистикой. */
    suspend fun listMyEvents(): List<OrgEventItem>

    /** Добавить участника (OWNER — любую роль; MANAGER — только STAFF). */
    suspend fun addMember(userId: String, role: String, venueId: String?): OrgMemberDto

    /** Добавить участника по номеру телефона. Создаёт аккаунт если не найден. */
    suspend fun addMemberByPhone(phone: String, role: String, venueId: String?): AddMemberByPhoneResponse

    /** Обновить роль/venue участника (только OWNER). */
    suspend fun updateMember(memberId: String, role: String, venueId: String?): OrgMemberDto

    /** Удалить участника (OWNER — любого; MANAGER — только STAFF). */
    suspend fun deleteMember(memberId: String)

    /** Покинуть организацию (текущий пользователь). */
    suspend fun leaveOrganization()
}
