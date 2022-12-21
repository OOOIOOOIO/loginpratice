package com.sh.loginpratice.rest_security_jwt_exception.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * MemberAuth 설명 : 회원들의 권한을 관리.
 * @author jowonjun
 * @version 1.0.0
 * 작성일 : 2022/02/27
 **/
public enum MemberAuth {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN"),

    ;

    private final String abbreviation;

    // 권한 조회 및 포함여부
    private static final Map<String,MemberAuth> lookup = new HashMap<>();

    static {
        for(MemberAuth auth : MemberAuth.values()) {
            lookup.put(auth.abbreviation,auth);
        }
    }

    // private
    MemberAuth(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return this.abbreviation;
    }

    public static MemberAuth get(String abbreviation) {
        return lookup.get(abbreviation);
    }

    public static boolean containsKey(String abbreviation) {
        return lookup.containsKey(abbreviation);
    }

}
