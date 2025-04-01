// components/ProjectHeader.jsx
import React from 'react';

const ProjectHeader = ({ projectData, sprintData }) => {
  const currentSprintData = sprintData[projectData.currentSprint];
  
  return (
    <div className="mb-12">
      <h1 className="text-2xl font-bold text-gray-800 mb-2">{projectData.name}</h1>
      <div className="flex flex-wrap gap-4 mb-4">
        <div className="bg-white p-3 rounded shadow flex-1 text-black">
          <p className="text-sm text-gray-800">Total de Horas</p>
          <p className="text-xl font-bold">{projectData.stats.totalEstimatedHours}</p>
        </div>
        <div className="bg-white p-3 rounded shadow flex-1 text-black">
          <p className="text-sm text-gray-800">Horas Completadas</p>
          <p className="text-xl font-bold">{projectData.stats.completedEstimatedHours}</p>
        </div>
        <div className="bg-white p-3 rounded shadow flex-1 text-black">
          <p className="text-sm text-gray-800">Horas Pendientes</p>
          <p className="text-xl font-bold">{projectData.stats.remainingHours}</p>
        </div>
        <div className="bg-white p-3 rounded shadow flex-1 text-black">
          <p className="text-sm text-gray-800">Completado</p>
          <p className="text-xl font-bold">{projectData.stats.completionPercentage}%</p>
        </div>
      </div>
      <div className="bg-blue-50 p-3 rounded border border-blue-200 mb-4">
        <p className="text-sm text-blue-800">
          <span className="font-bold text-black">Sprint Actual:</span> {projectData.currentSprint}
          <span className="ml-4 font-bold text-gray-800">Fecha Inicio:</span> {currentSprintData?.startDate}
          <span className="ml-4 font-bold text-gray-800">Fecha Fin:</span> {currentSprintData?.endDate}
        </p>
      </div>
    </div>
  );
};

export default ProjectHeader;