package com.jemmazz.conveyance.init;

import com.jemmazz.conveyance.Conveyance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ConveyanceSounds
{
    private ConveyanceSounds()
    {
        // NO-OP
    }

    public static void init()
    {
        // NO-OP
    }

    private static SoundEvent register( String name )
    {
        return Registry.register( Registry.SOUND_EVENT, Conveyance.id( name ), new SoundEvent( new Identifier( "conveyance", name ) ) );
    }
}
