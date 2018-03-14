package de.dwslab.ki.joerg.thesis.risk.infrastructure.generator;

public final class InfrastructureConfiguration {

	public static final int MIN_POWER_SOURCE_PER_INFRA = 1;
	public static final int MAX_POWER_SOURCE_PER_INFRA = 2;

	public static final int MIN_INFRA_PER_POWER_SOURCE = 0;
	public static final int MAX_INFRA_PER_POWER_SOURCE = 50;

	public static final int MIN_INFRA_PER_INFRA = 0;
	public static final int MAX_INFRA_PER_INFRA = 20;

	public static final int MIN_SERVICE_PER_INFRA = 0;
	public static final int MAX_SERVICE_PER_INFRA = 5;

	public static final int MIN_INFRA_PER_SERVICE = 1;
	public static final int MAX_INFRA_PER_SERVICE = 10;

	public static final int MIN_SERVICE_PER_SERVICE = 0;
	public static final int MAX_SERVICE_PER_SERVICE = 20;

	public static final int MIN_RISK_PER_COMPONENT = 1;
	public static final int MAX_RISK_PER_COMPONENT = 10;

	public static final double MAX_RISK_PROBABILITY = 0.1;

	public static final double POWER_SOURCE_REDUNDANCY = 0.4;
	public static final int MAX_POWER_SOURCE_REDUNDANCY = 3;

	public static final double INFRA_REDUNDANCY = 0.5;
	public static final int MAX_INFRA_REDUNDANCY = 5;

	public static final double SERVICE_REDUNDANCY = 0.20;
	public static final int MAX_SERVICE_REDUNDANCY = 10;

}
