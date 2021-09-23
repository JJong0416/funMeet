package com.funmeet.infra;

import org.testcontainers.containers.PostgreSQLContainer;

public abstract class AbstractContainerBaseTest {

    static final PostgreSQLContainer POSTGRE_SQL_CONTAINER;

    static{ // 클래스 로딩할 때, 한번 호출 이거 안하면 테스트 하나하나에 컨테이너 뛰어야해서 속도 개느림
        POSTGRE_SQL_CONTAINER = new PostgreSQLContainer();
        POSTGRE_SQL_CONTAINER.start();
    }
}
