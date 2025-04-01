// components/TaskTable.jsx
import React from 'react';

const TaskTable = ({ tasks, currentSprint }) => {
  const currentSprintTasks = tasks.filter(task => task.sprint === currentSprint);
  
  return (
    <div className="mt-6">
      <h2 className="text-lg font-bold text-gray-800 mb-2">Tareas del Sprint Actual</h2>
      <div className="bg-white rounded shadow overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="bg-gray-200">
            <tr>
              <th className="p-2 text-black text-left">ID</th>
              <th className="p-2 text-black text-left">Tarea</th>
              <th className="p-2 text-black text-left">Asignado a</th>
              <th className="p-2 text-black text-right">Horas Est.</th>
              <th className="p-2 text-black text-right">Horas Real</th>
              <th className="p-2 text-black text-center">Estado</th>
            </tr>
          </thead>
          <tbody>
            {currentSprintTasks.map(task => (
              <tr key={task.id} className={task.status === "Done" ? "bg-green-50" : ""}>
                <td className="p-2 text-black border-t">{task.id}</td>
                <td className="p-2 text-black border-t">{task.title}</td>
                <td className="p-2 text-black border-t">{task.assignees.join(', ')}</td>
                <td className="p-2 text-black border-t text-right">{task.estimate}</td>
                <td className="p-2 text-black border-t text-right">{task.actual !== null ? task.actual : '-'}</td>
                <td className="p-2 text-black border-t text-center">
                  <span className={`inline-block rounded-full px-2 py-1 text-xs font-semibold ${
                    task.status === "Done" ? 'bg-green-200 text-green-800' : 
                    task.status === "In progress" ? 'bg-blue-200 text-blue-800' :
                    task.status === "Ready" ? 'bg-yellow-200 text-yellow-800' :
                    'bg-gray-200 text-gray-800'
                  }`}>
                    {task.status}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default TaskTable;