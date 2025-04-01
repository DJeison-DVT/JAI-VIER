// components/BurnDownChart.jsx
import React from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, ReferenceLine } from 'recharts';
import { colors, today } from '../utils/projectData';

const BurnDownChart = ({ chartData, showIdeal, showActual }) => {
  return (
    <div className="bg-white p-4 rounded shadow-md">
      <div className="h-125">
        <ResponsiveContainer width="100%" height="100%">
          <LineChart
            data={chartData}
            margin={{ top: 15, right: 30, left: 20, bottom: 5 }}
          >
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="date" />
            <YAxis label={{ value: 'Horas Restantes', angle: -90, position: 'insideLeft' }} />
            <Tooltip />
            <Legend />
            {showIdeal && (
              <Line 
                type="monotone" 
                dataKey="ideal" 
                stroke={colors.ideal} 
                name="Trabajo Ideal Restante" 
                strokeWidth={2}
              />
            )}
            {showActual && (
              <Line 
                type="monotone" 
                dataKey="actual" 
                stroke={colors.actual} 
                name="Trabajo Real Restante" 
                strokeWidth={2}
              />
            )}
            <ReferenceLine x={today} stroke={colors.today} label="Hoy" />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
};

export default BurnDownChart;