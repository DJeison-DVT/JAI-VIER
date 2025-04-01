// components/ControlChart.jsx
import React, { useState, useEffect } from 'react';
import { 
  ScatterChart, 
  Scatter, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  ResponsiveContainer,
  ReferenceLine,
  Line,
  ComposedChart,
  Legend
} from 'recharts';
import { format, parseISO, differenceInDays } from 'date-fns';

const ControlChart = ({ tasks, selectedSprint }) => {
  const [chartData, setChartData] = useState([]);
  const [meanCycleTime, setMeanCycleTime] = useState(0);
  const [upperControl, setUpperControl] = useState(0);
  const [lowerControl, setLowerControl] = useState(0);

  useEffect(() => {
    // Filtrar tareas completadas
    const completedTasks = tasks.filter(task => 
      task.status === "Done" && 
      (selectedSprint === 'all' || task.sprint === selectedSprint)
    );
    
    if (completedTasks.length === 0) {
      setChartData([]);
      setMeanCycleTime(0);
      setUpperControl(0);
      setLowerControl(0);
      return;
    }

    // Calcular tiempo de ciclo para cada tarea completada
    const tasksWithCycleTime = completedTasks.map(task => {
      // Calcular tiempo de ciclo (días desde creación hasta completado)
      // Asumimos que la tarea fue creada 7 días antes para este ejemplo
      // En un sistema real, necesitarías la fecha de creación real
      const completionDate = parseISO(task.completionDate);
      
      // Simulamos una fecha de creación 
      // (En tu sistema real, usa la fecha real de creación de la tarea)
      const creationDate = new Date(completionDate);
      creationDate.setDate(creationDate.getDate() - (task.actual || 0) - 7); 
      
      const cycleTime = differenceInDays(completionDate, creationDate);
      
      return {
        id: task.id,
        title: task.title,
        date: task.completionDate,
        cycleTime: cycleTime > 0 ? cycleTime : 1, // Aseguramos valor positivo
        sprint: task.sprint,
        assignees: task.assignees.join(', ')
      };
    });
    
    // Ordenar por fecha de completado
    const sortedTasks = [...tasksWithCycleTime].sort((a, b) => 
      new Date(a.date) - new Date(b.date)
    );
    
    // Calcular media y desviación estándar
    const cycleTimes = sortedTasks.map(task => task.cycleTime);
    const mean = cycleTimes.reduce((sum, time) => sum + time, 0) / cycleTimes.length;
    
    const squaredDiffs = cycleTimes.map(time => Math.pow(time - mean, 2));
    const variance = squaredDiffs.reduce((sum, sqDiff) => sum + sqDiff, 0) / cycleTimes.length;
    const stdDev = Math.sqrt(variance);
    
    // Establecer límites de control (media ± 2 desviaciones estándar)
    const upper = mean + 2 * stdDev;
    const lower = Math.max(1, mean - 2 * stdDev); // No permitimos valores negativos
    
    setChartData(sortedTasks);
    setMeanCycleTime(mean);
    setUpperControl(upper);
    setLowerControl(lower);
  }, [tasks, selectedSprint]);

  // Formatear tooltip
  const CustomTooltip = ({ active, payload }) => {
    if (active && payload && payload.length) {
      const data = payload[0].payload;
      return (
        <div className="bg-white p-3 border rounded shadow-md">
          <p className="font-bold">{data.title}</p>
          <p>ID: {data.id}</p>
          <p>Sprint: {data.sprint}</p>
          <p>Cycle Time: {data.cycleTime} días</p>
          <p>Completado: {format(new Date(data.date), 'dd/MM/yyyy')}</p>
          <p>Asignado a: {data.assignees}</p>
        </div>
      );
    }
    return null;
  };

  // Si no hay datos, mostrar mensaje
  if (chartData.length === 0) {
    return (
      <div className="bg-white p-4 rounded shadow my-4">
        <h3 className="text-xl font-bold mb-4">Control Chart - Tiempo de Ciclo</h3>
        <div className="text-center py-10">
          <p className="text-gray-500">No hay tareas completadas para mostrar</p>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white p-4 rounded shadow my-4">
      <h3 className="text-xl text-black font-bold mb-4">Control Chart - Tiempo de Ciclo</h3>
      
      <div className="mb-4">
        <div className="grid grid-cols-3 gap-4">
          <div className="bg-blue-50 p-3 rounded text-center">
            <p className="text-sm text-gray-500">Tiempo Medio</p>
            <p className="font-bold text-blue-600">{meanCycleTime.toFixed(1)} días</p>
          </div>
          <div className="bg-red-50 p-3 rounded text-center">
            <p className="text-sm text-gray-500">Límite Superior</p>
            <p className="font-bold text-red-600">{upperControl.toFixed(1)} días</p>
          </div>
          <div className="bg-green-50 p-3 rounded text-center">
            <p className="text-sm text-gray-500">Límite Inferior</p>
            <p className="font-bold text-green-600">{lowerControl.toFixed(1)} días</p>
          </div>
        </div>
      </div>
      
      <div className="h-80 text-black">
        <ResponsiveContainer width="100%" height="100%">
          <ComposedChart
            margin={{ top: 10, right: 50, bottom: 50, left: 20 }}
            data={chartData}
          >
            <CartesianGrid />
            <XAxis 
              dataKey="date" 
              name="Fecha" 
              tickFormatter={(date) => format(new Date(date), 'dd/MM')}
              type="category"
              label={{ value: 'Fecha de Completado', position: 'inside', offset: -10 }}
            />
            <YAxis 
              dataKey="cycleTime" 
              name="Tiempo de Ciclo (días)" 
              label={{ value: 'Tiempo de Ciclo (días)', angle: -90, position: 'Left', offset: 40}}    
            />
            <Tooltip content={<CustomTooltip />} />
            <Legend />
            
            {/* Líneas de referencia para los límites de control */}
            <ReferenceLine y={meanCycleTime} stroke="#3b82f6" strokeDasharray="3 3" label={{ value: 'Media', position: 'right', fill: '#3b82f6' }} />
            <ReferenceLine y={upperControl} stroke="#ef4444" strokeDasharray="3 3" label={{ value: 'UCL', position: 'right', fill: '#ef4444' }} />
            <ReferenceLine y={lowerControl} stroke="#22c55e" strokeDasharray="3 3" label={{ value: 'LCL', position: 'right', fill: '#22c55e' }} />
            
            {/* Línea que conecta los puntos */}
            <Line 
              type="monotone" 
              dataKey="cycleTime" 
              stroke="#8884d8" 
              strokeWidth={2}
              dot={{ fill: '#8884d8', r: 6 }}
              activeDot={{ r: 8 }}
              name="Tiempo de Ciclo"
              connectNulls
            />
          </ComposedChart>
        </ResponsiveContainer>
      </div>
      
      <div className="mt-4 p-3 bg-gray-50 rounded text-sm">
        <h4 className="font-bold text-black mb-2">¿Cómo interpretar este gráfico?</h4>
        <ul className="list-disc pl-5 space-y-1 text-black">
          <li><span className="font-medium text-blue-600">Línea central (azul)</span>: Tiempo de ciclo promedio.</li>
          <li><span className="font-medium text-red-600">Línea superior (roja)</span>: Límite de control superior (tareas que tomaron más tiempo del esperado).</li>
          <li><span className="font-medium text-green-600">Línea inferior (verde)</span>: Límite de control inferior.</li>
          <li><span className="font-medium text-purple-600">Línea de tendencia (morada)</span>: Conecta los puntos para mostrar la evolución del tiempo de ciclo.</li>
          <li>Los puntos por encima del límite superior indican tareas que tomaron considerablemente más tiempo y requieren análisis.</li>
          <li>Un proceso estable tendrá la mayoría de los puntos entre los límites de control.</li>
          <li>Las tendencias ascendentes indican que el proceso se está deteriorando (más tiempo), mientras que las descendentes muestran mejora (menos tiempo).</li>
        </ul>
      </div>
    </div>
  );
};

export default ControlChart;