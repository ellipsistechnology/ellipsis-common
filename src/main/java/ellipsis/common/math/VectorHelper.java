package ellipsis.common.math;

import static ellipsis.common.ArrayHelper.array;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import ellipsis.common.math.Sum.IndexedFunction;
import ellipsis.common.math.Sum.IndexedNumberFunction;
import ellipsis.common.math.Sum.ValueFunction;
import ellipsis.common.math.Sum.VectorValueFunction;

public class VectorHelper
{
	public static String printVector(RealVector v)
	{
		if(v == null)
			return null;
		
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < v.getDimension(); ++i)
		{
			sb.append(v.getEntry(i));
			sb.append(',');
		}
		return sb.toString();
	}
	
	public static void abs(RealVector v)
	{
		int dimension = v.getDimension();
		for (int i = 0; i < dimension; i++)
		{
			double v_i = v.getEntry(i);
			v.setEntry(i, Math.abs(v_i));
		}
	}

	public static RealVector vector(double... values)
	{
		return new ArrayRealVector(values);
	}
	
	public static RealVector vector(Double[] values)
	{
		return new ArrayRealVector(values);
	}
	
	public static RealVector vector(int length, double preset)
	{
		double[] data = array(length, preset);
		return new ArrayRealVector(data);
	}
	
	public static RealVector vector(int length, IndexedFunction initialiser)
	{
	    return vector(length, initialiser, -1);
	}
	
	public static RealVector vector(int length, IndexedFunction initialiser, int ignore)
	{
		double[] a = new double[length];
		for (int i = 0; i < length; i++)
		{
		    if(i == ignore)
		        continue;
			a[i] = initialiser.value(i);
		}
		return new ArrayRealVector(a);
	}
    
    public static void modifyVector(RealVector v, IndexedFunction modifier)
    {
        int length = v.getDimension();
        for (int i = 0; i < length; i++)
        {
            v.setEntry(i, modifier.value(i));
        }
    }

    public static <T> RealVector vector(Collection<T> ts, ValueFunction<T> fn)
    {
    	RealVector v = new ArrayRealVector(ts.size());
        int i = 0;
        for (T t : ts)
		{
			v.setEntry(i, fn.value(t));
			++i;
        }
        return v;
    }

    public static <T> RealVector appendVectors(Collection<T> ts, VectorValueFunction<T> fn)
    {
    	RealVector v = new ArrayRealVector();
        for (T t : ts)
		{
			v = v.append(fn.value(t));
        }
        return v;
    }

	public static boolean equals(RealVector v1, RealVector v2, double d)
	{
		for (int i = 0; i < v1.getDimension(); i++)
		{
			if((v1.getEntry(i) - v2.getEntry(i)) > d)
				return false;
		}
		return true;
	}

	public static RealVector one(int dimension)
	{
		return vector(dimension, 1.0);
	}

	/**
	 * Creates a vector array from the given data.
	 * @param vectorCount The dimension of the returned array.
	 * @param elementsPerVector The dimension of each vector.
	 * @param data The data to put into the vectors. Must have dimension elementsPerVector*vectorCount.
	 * @return
	 */
	public static RealVector[] vectorArray(int vectorCount, int elementsPerVector, double... data)
	{
		RealVector[] vs = new RealVector[vectorCount];
		int k = 0;
		for(int i = 0; i < vectorCount; ++i)
		{
			vs[i] = new ArrayRealVector(Arrays.copyOfRange(data, k, k+elementsPerVector));
			k += elementsPerVector;
		}
		return vs;
	}

	/**
	 * Sums the elements of the vector.
	 * @param vDiff
	 * @return
	 */
	public static double sum(RealVector v)
	{
		double sum = 0;
		int dimension = v.getDimension();
		for (int i = 0; i < dimension; i++)
		{
			sum += v.getEntry(i);
		}
		return sum;
	}
    
    public static double average(RealVector v)
    {
    	double dimension = v.getDimension();
    	return sum(v)/dimension;
    }
	
	public static double normSquared(RealVector v)
	{
	    return normSquared(v.toArray());
	}

    public static double normSquared(double... ds)
    {
        return Sum.sum(ds, d -> d*d);
    }
    
    public static double max(RealVector v)
    {
    	return max(v.getDimension(), v::getEntry);
    }
    
    public static double max(int length, IndexedFunction f)
    {
    	double max = -Double.MAX_VALUE;
		for (int i = 0; i < length; i++)
		{
			max = Math.max(f.value(i), max);
		}
		return max;
    }
    
    public static double maxAbs(RealVector v)
    {
    	return maxAbs(v.getDimension(), v::getEntry);
    }
    
    public static double maxAbs(int length, IndexedFunction f)
    {
    	return max(length, i -> Math.abs(f.value(i)));
    }
    
    public static RealVector map(RealVector vIn, IndexedNumberFunction f)
    {
    	int dimension = vIn.getDimension();
		RealVector vOut = new ArrayRealVector(dimension);
    	for (int i = 0; i < dimension; i++) 
    	{
			vOut.setEntry(i, f.value(i, vIn.getEntry(i)));
		}
    	return vOut;
    }
}
