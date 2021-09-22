package com.funmeet;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packagesOf = FunMeet.class)
public class PackageDependencyTests {

    private static final String CLUB = "..modules.club..";
    private static final String MEETING = "..modules.meeting..";
    private static final String ACCOUNT = "..modules.account..";
    private static final String HOBBY = "..modules.hobby..";
    private static final String CITY = "..modules.city..";
    private static final String MAIN = "..modules.main..";

    /* 어플리케이션의 아키텍처를 테스트 할 수 있는 오픈 소스 라이브러리로 패키지, 클래스, 레이어, 슬라이스 간 의존성을 확인할 수 있는 기능을 제공한다. */
    /* Ref : https://www.archunit.org/userguide/html/000_Index.html */

    @ArchTest
    ArchRule modulesPackageRule = classes().that().resideInAPackage("com.funmeet.modules..")
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage("com.funmeet.modules..");

    @ArchTest
    ArchRule clubPackageRule = classes().that().resideInAPackage("..modules.club..")
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage(MEETING, CLUB, MAIN);


    @ArchTest
    ArchRule accountPackageRule = classes().that().resideInAPackage(ACCOUNT)
            .should().accessClassesThat().resideInAnyPackage(CITY, HOBBY, ACCOUNT);

    @ArchTest
    ArchRule meetingPackageRule = classes().that().resideInAPackage(MEETING)
            .should().accessClassesThat().resideInAnyPackage(MEETING, ACCOUNT, CLUB);

    /*  순환참조에 사이클이 발생하는지 체크*/
    @ArchTest
    ArchRule cycleCheck = slices().matching("com.funmeet.modules.(*)..")
            .should().beFreeOfCycles();

}