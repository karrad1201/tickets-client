package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.OrgMemberDto
import com.karrad.ticketsclient.data.api.dto.OrgMembershipDto

interface OrgMemberService {
    /** Возвращает членство текущего пользователя в организации, или null если не состоит. */
    suspend fun getMyMembership(): OrgMembershipDto?

    /** Список членов организации (требует OWNER или MANAGER). */
    suspend fun listMembers(): List<OrgMemberDto>

    /** Добавить участника (OWNER — любую роль; MANAGER — только STAFF). */
    suspend fun addMember(userId: String, role: String, venueId: String?): OrgMemberDto

    /** Обновить роль/venue участника (только OWNER). */
    suspend fun updateMember(memberId: String, role: String, venueId: String?): OrgMemberDto

    /** Удалить участника (OWNER — любого; MANAGER — только STAFF). */
    suspend fun deleteMember(memberId: String)
}
