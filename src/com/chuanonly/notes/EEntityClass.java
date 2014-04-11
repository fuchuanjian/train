package com.chuanonly.notes;

public enum EEntityClass
{
	EEntityTrain,
	EEntityDoodad;

	public int getValue()
	{
		return this.ordinal();
	}

	public static EEntityClass forValue(int value)
	{
		return values()[value];
	}
}