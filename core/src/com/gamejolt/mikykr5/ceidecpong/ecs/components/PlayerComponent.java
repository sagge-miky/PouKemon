/*
 * Copyright (c) 2014, Miguel Angel Astor Romero
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * Read the LICENSE file for more details.
 */
package com.gamejolt.mikykr5.ceidecpong.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * A Player identity {@link Component}
 * 
 * @author Miguel Astor
 */
public class PlayerComponent extends Component implements Poolable {
	public static final int HUMAN_PLAYER = 0;
	public static final int COMPUTER_PLAYER = 1;

	/**
	 * An identifier used to determine the type of player.
	 */
	public int id = -1;

	@Override
	public void reset() {
		id = -1;
	}
}
