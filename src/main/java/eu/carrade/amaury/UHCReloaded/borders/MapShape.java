/*
 * Copyright or © or Copr. Amaury Carrade (2014 - 2016)
 *
 * http://amaury.carrade.eu
 *
 * This software is governed by the CeCILL-B license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-B
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-B license and that you accept its terms.
 */
package eu.carrade.amaury.UHCReloaded.borders;

import eu.carrade.amaury.UHCReloaded.borders.generators.CircularWallGenerator;
import eu.carrade.amaury.UHCReloaded.borders.generators.SquaredWallGenerator;
import eu.carrade.amaury.UHCReloaded.borders.generators.WallGenerator;
import eu.carrade.amaury.UHCReloaded.borders.shapes.CircularMapShape;
import eu.carrade.amaury.UHCReloaded.borders.shapes.MapShapeDescriptor;
import eu.carrade.amaury.UHCReloaded.borders.shapes.SquaredMapShape;
import fr.zcraft.zlib.tools.PluginLogger;
import org.bukkit.Material;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public enum MapShape
{
    CIRCULAR(new CircularMapShape(), CircularWallGenerator.class),
    SQUARED(new SquaredMapShape(), SquaredWallGenerator.class);


    private MapShapeDescriptor shape;
    private Class<? extends WallGenerator> generatorClass;

    /**
     * @param generator The wall generator class associated with this shape.
     */
    MapShape(MapShapeDescriptor shape, Class<? extends WallGenerator> generator)
    {
        this.shape = shape;
        this.generatorClass = generator;
    }

    /**
     * Returns a new instance of the wall generator for this shape.
     *
     * @return The instance.
     */
    public WallGenerator getWallGeneratorInstance(Material wallBlockAir, Material wallBlockSolid)
    {
        try
        {
            Constructor constructor = generatorClass.getConstructor(Material.class, Material.class);
            return (WallGenerator) constructor.newInstance(wallBlockAir, wallBlockSolid);

        }
        catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e)
        {
            PluginLogger.error("Cannot instantiate the walls generator: invalid class.", e);
            return null;
        }
    }

    /**
     * Returns the shape descriptor.
     *
     * @return The shape.
     */
    public MapShapeDescriptor getShape()
    {
        return shape;
    }

    /**
     * Returns a shape based on his name.
     *
     * <p>Not case sensitive.</p>
     *
     * @param name The name.
     * @return The MapShape, or {@code null} if not found.
     */
    public static MapShape fromString(String name)
    {
        try
        {
            return MapShape.valueOf(name.trim().toUpperCase());
        }
        catch (IllegalArgumentException e)
        {
            return null;
        }
    }
}
