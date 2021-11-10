package mr.go.sgfilter.tests;

import static org.junit.Assert.assertEquals;
import mr.go.sgfilter.ContinuousPadder;
import mr.go.sgfilter.Linearizer;
import mr.go.sgfilter.MeanValuePadder;
import mr.go.sgfilter.RamerDouglasPeuckerFilter;
import mr.go.sgfilter.SGFilter;
import mr.go.sgfilter.ZeroEliminator;

import org.junit.Test;


public class FilterTestCase {


        double[] coeffs = SGFilter.computeSGCoefficients(5,
                5,
                4);