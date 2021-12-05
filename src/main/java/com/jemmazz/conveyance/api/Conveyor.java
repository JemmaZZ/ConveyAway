package com.jemmazz.conveyance.api;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;

public interface Conveyor
{
    BooleanProperty BACK = BooleanProperty.of( "back" );
    BooleanProperty FRONT = BooleanProperty.of( "front" );
    BooleanProperty TOP = BooleanProperty.of( "top" );

    boolean isFlat();

    /**
     * Gets the speed of a conveyor in ticks/cycle.
     *
     * @return Speed of conveyor.
     */
    double getSpeed();

    /**
     * Used for grabbing the texture.
     *
     * @return Identifier of the conveyor.
     */
    Identifier getId();

    /**
     * Set the identifier of the conveyor.
     *
     * @param id Identifier of the conveyor.
     */
    void setId( Identifier id );
}
