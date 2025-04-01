// components/WorkloadChart.jsx
import React from "react";
import {
	BarChart,
	Bar,
	XAxis,
	YAxis,
	CartesianGrid,
	Tooltip,
	Legend,
	ResponsiveContainer,
} from "recharts";

const WorkloadChart = ({ memberData, selectedMember }) => {
	// Preparar datos para el gráfico
	const getChartData = () => {
		if (selectedMember === "all") {
			return Object.keys(memberData).map((member) => ({
				name: member,
				estimatedHours: memberData[member].estimatedHours,
				completedTasks: memberData[member].completedTasks,
			}));
		} else {
			return [
				{
					name: selectedMember,
					estimatedHours: memberData[selectedMember].estimatedHours,
					completedTasks: memberData[selectedMember].completedTasks,
				},
			];
		}
	};

	const data = getChartData();

	return (
		<div className="bg-white p-4 rounded shadow-md">
			<div className="h-80">
				<ResponsiveContainer width="100%" height="100%">
					<BarChart
						data={data}
						margin={{ top: 50, right: 30, left: 20, bottom: 5 }}
					>
						<CartesianGrid strokeDasharray="3 3" />
						<XAxis dataKey="name" />
						<YAxis
							label={{
								value: "Horas Estimadas",
								angle: -90,
								position: "insideLeft",
							}}
						/>
						<Tooltip />
						<Legend />
						<Bar
							dataKey="estimatedHours"
							fill="#8884d8"
							name="Horas Estimadas"
						/>
						<Bar
							dataKey="completedTasks"
							fill="#82ca9d"
							name="Tareas Completadas"
						/>
					</BarChart>
				</ResponsiveContainer>
			</div>
		</div>
	);
};

export default WorkloadChart;
