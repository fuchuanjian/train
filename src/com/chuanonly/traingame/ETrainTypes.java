package com.chuanonly.traingame;

public enum ETrainTypes
{
	ETrainEngine,
	ETrainCoal,
	ETrainCarriage;

	public int getValue()
	{
		return this.ordinal();
	}

	public static ETrainTypes forValue(int value)
	{
		return values()[value];
	}
}