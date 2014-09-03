﻿package com.chuanonly.traingame;

public enum EParticleTypes
{
	EParticleSmoke,
	EParticleExplosion;

	public int getValue()
	{
		return this.ordinal();
	}

	public static EParticleTypes forValue(int value)
	{
		return values()[value];
	}
}