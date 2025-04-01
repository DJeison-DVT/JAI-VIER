// components/StatusChart.jsx
import React from 'react';
import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { colors } from '../utils/projectData';

const StatusChart = ({ projectData }) => {
  const taskStatusData = [
    { name: 'Completadas', value: projectData.stats.completedTasks },
    { name: 'En Progreso', value: 1 }, // Según los datos, solo 1 tarea está en progreso
    { name: 'Pendientes', value: projectData.stats.remainingTasks - 1 }
  ];

  return (
    <div className="bg-white p-4 rounded shadow-md">
      <div className="h-64">
        <ResponsiveContainer width="100%" height="100%">
          <PieChart>
            <Pie
              data={taskStatusData}
              cx="50%"
              cy="50%"
              labelLine={true}
              label={({name, percent}) => `${name}: ${(percent * 100).toFixed(0)}%`}
              outerRadius={80}
              fill="#8884d8"
              dataKey="value"
            >
              {taskStatusData.map((entry, index) => (
                <Cell 
                  key={`cell-${index}`} 
                  fill={
                    index === 0 ? colors.completed : 
                    index === 1 ? colors.inProgress : 
                    colors.pending
                  } 
                />
              ))}
            </Pie>
            <Tooltip />
            <Legend />
          </PieChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
};

export default StatusChart;