package com.jemmazz.conveyaway.api;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;

public interface Conveyor {
    BooleanProperty BACK = BooleanProperty.of("back");
    BooleanProperty FRONT = BooleanProperty.of("front");

    /**
     * Gets the speed of a conveyor in ticks/cycle.
     *
     * @return Speed of conveyor.
     */
    int getSpeed();

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
    void setId(Identifier id);
}
