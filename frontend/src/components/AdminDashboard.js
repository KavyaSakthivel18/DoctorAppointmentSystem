import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./Dashboard.css";

import API from "../apiConfig";

function AdminDashboard() {
  const navigate = useNavigate();
  const email = localStorage.getItem("email");

  const [activeTab, setActiveTab] = useState("overview");
  const [appointments, setAppointments] = useState([]);
  const [doctors, setDoctors] = useState([]);
  const [alert, setAlert] = useState(null);
  const [showAddDoctor, setShowAddDoctor] = useState(false);
  const [loading, setLoading] = useState(false);

  const [doctorForm, setDoctorForm] = useState({
    name: "", email: "", password: "",
    specialization: "", department: "", mode: "ONLINE"
  });

  const [reportData, setReportData] = useState({
    totalAppointments: 0, revenue: 0,
    booked: 0, confirmed: 0, cancelled: 0
  });

  useEffect(() => {
    fetchAppointments();
    fetchDoctors();
    fetchReports();
  }, []);

  async function fetchAppointments() {
    const role = localStorage.getItem("role");
    const email = localStorage.getItem("email");
    try {
      const res = await API.get("/appointments", {
        headers: {
          "X-User-Email": email,
          "X-User-Role": role
        }
      });
      setAppointments(res.data);
    } catch (e) { console.error(e); }
  }

  async function fetchDoctors() {
    try {
      const res = await API.get("/public/doctors");
      setDoctors(res.data);
    } catch (e) { console.error(e); }
  }

  async function fetchReports() {
    try {
      const dateStr = new Date().toISOString().split("T")[0];
      const summaryRes = await API.get(`/reports/daily-summary?date=${dateStr}`);
      setReportData(prev => ({
        ...prev,
        totalAppointments: summaryRes.data.totalAppointments,
        revenue: summaryRes.data.revenue
      }));
    } catch (e) { console.error("Failed to fetch reports", e); }
  }

  async function handleDeleteDoctor(id) {
    if (!window.confirm("Are you sure you want to delete this doctor? This action cannot be undone.")) return;
    try {
      const res = await API.delete(`/admin/doctors/${id}`);
      if (res.status === 200) {
        showAlert("Doctor deleted successfully.", "success");
        fetchDoctors();
        // optionally fetch appointments/reports again since appointments might be deleted
        fetchAppointments();
        fetchReports();
      }
    } catch (e) {
      showAlert("Failed to delete doctor.", "error");
    }
  }

  async function handleCancelAppointment(id) {
    if (!window.confirm("Cancel this appointment?")) return;
    try {
      const res = await API.put(`/appointments/${id}/cancel`);
      if (res.status === 200) {
        showAlert("Appointment cancelled.", "success");
        fetchAppointments();
      }
    } catch (e) { showAlert("Failed to cancel or Network error.", "error"); }
  }

  async function handleAddDoctor(e) {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await API.post("/auth/register", {
        ...doctorForm,
        departmentName: doctorForm.department, // Map 'department' to 'departmentName'
        role: "DOCTOR"
      });
      if (res.status === 200 || res.status === 201) {
        showAlert("Doctor added successfully!", "success");
        setShowAddDoctor(false);
        setDoctorForm({ name: "", email: "", password: "", specialization: "", department: "", mode: "ONLINE" });
        fetchDoctors();
      }
    } catch (e) {
      const err = e.response?.data?.message || e.response?.data || "Failed to add doctor.";
      showAlert(err, "error");
    }
    finally { setLoading(false); }
  }

  function showAlert(msg, type) {
    setAlert({ msg, type });
    setTimeout(() => setAlert(null), 3500);
  }

  function handleLogout() {
    localStorage.clear();
    navigate("/");
  }

  const confirmed = appointments.filter(a => a.status === "CONFIRMED").length;
  const booked = appointments.filter(a => a.status === "BOOKED").length;
  const cancelled = appointments.filter(a => a.status === "CANCELLED").length;
  // const completed  = appointments.filter(a => a.status === "COMPLETED").length;

  return (
    <div className="dashboard-layout">
      <div className="mesh-gradient"></div>
      {/* Sidebar */}
      <aside className="sidebar">
        <div className="sidebar-logo">Medi<span>Sync</span><br /><small style={{ fontSize: '0.8rem', opacity: 0.8 }}>Admin Panel</small></div>
        <nav className="sidebar-nav">
          {[
            { key: "overview", icon: "📊", label: "Overview" },
            { key: "appointments", icon: "📋", label: "Appointments" },
            { key: "doctors", icon: "👨‍⚕️", label: "Doctors" },
          ].map(item => (
            <div
              key={item.key}
              className={`nav-item ${activeTab === item.key ? "active" : ""}`}
              onClick={() => setActiveTab(item.key)}
            >
              <span className="nav-icon">{item.icon}</span> {item.label}
            </div>
          ))}
        </nav>
        <div className="sidebar-footer">
          <div style={{ color: "#94a3b8", fontSize: "0.78rem", marginBottom: 10 }}>{email}</div>
          <button className="logout-btn" onClick={handleLogout}>Sign Out</button>
        </div>
      </aside>

      {/* Main */}
      <main className="main-content">
        {alert && <div className={`alert alert-${alert.type}`}>{alert.msg}</div>}

        {/* OVERVIEW */}
        {activeTab === "overview" && (
          <>
            <div className="page-header">
              <h1>Dashboard Overview</h1>
              <p>Hospital at a glance</p>
            </div>
            <div className="stats-row" style={{ gridTemplateColumns: "repeat(3, 1fr)" }}>
              <div className="stat-card">
                <div className="stat-icon blue">👨‍⚕️</div>
                <div className="stat-info"><p>Doctors</p><h3>{doctors.length}</h3></div>
              </div>
              <div className="stat-card">
                <div className="stat-icon indigo">📋</div>
                <div className="stat-info"><p>Total Appointments</p><h3>{reportData.totalAppointments > 0 ? reportData.totalAppointments : appointments.length}</h3></div>
              </div>
              <div className="stat-card">
                <div className="stat-icon green">💰</div>
                <div className="stat-info"><p>Daily Revenue</p><h3>${reportData.revenue}</h3></div>
              </div>
              <div className="stat-card">
                <div className="stat-icon amber">⏳</div>
                <div className="stat-info"><p>Booked</p><h3>{booked}</h3></div>
              </div>
              <div className="stat-card">
                <div className="stat-icon green">✅</div>
                <div className="stat-info"><p>Confirmed</p><h3>{confirmed}</h3></div>
              </div>
              <div className="stat-card">
                <div className="stat-icon red">❌</div>
                <div className="stat-info"><p>Cancelled</p><h3>{cancelled}</h3></div>
              </div>
            </div>

            {/* Recent appointments */}
            <div className="card">
              <div className="card-header"><h3>Recent Appointments</h3></div>
              <div className="card-body">
                <AppointmentTable
                  appointments={appointments.slice(0, 5)}
                  onCancel={handleCancelAppointment}
                  showCancel
                />
              </div>
            </div>
          </>
        )}

        {/* APPOINTMENTS */}
        {activeTab === "appointments" && (
          <>
            <div className="page-header">
              <h1>All Appointments</h1>
              <p>Manage and cancel scheduled appointments</p>
            </div>
            <div className="card">
              <div className="card-body">
                <AppointmentTable
                  appointments={appointments}
                  onCancel={handleCancelAppointment}
                  showCancel
                />
              </div>
            </div>
          </>
        )}

        {/* DOCTORS */}
        {activeTab === "doctors" && (
          <>
            <div className="page-header" style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
              <div>
                <h1>Doctors</h1>
                <p>Manage hospital doctors</p>
              </div>
              <button className="btn btn-primary" onClick={() => setShowAddDoctor(true)}>+ Add Doctor</button>
            </div>
            <div className="card">
              <div className="card-body table-wrap">
                {doctors.length === 0 ? (
                  <div className="empty-state"><div className="empty-icon">👨‍⚕️</div><p>No doctors yet.</p></div>
                ) : (
                  <table>
                    <thead>
                      <tr>
                        <th>#</th><th>Name</th><th>Email</th>
                        <th>Specialization</th><th>Department</th><th>Mode</th><th>Action</th>
                      </tr>
                    </thead>
                    <tbody>
                      {doctors.map((d, i) => (
                        <tr key={d.id}>
                          <td>{i + 1}</td>
                          <td><strong>{d.name}</strong></td>
                          <td>{d.email}</td>
                          <td>{d.specialization || "—"}</td>
                          <td>{d.department?.name || "—"}</td>
                          <td>{d.mode || "—"}</td>
                          <td>
                            <button className="btn btn-danger btn-sm" onClick={() => handleDeleteDoctor(d.id)}>Delete</button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                )}
              </div>
            </div>
          </>
        )}
      </main>

      {/* Add Doctor Modal */}
      {showAddDoctor && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>Add New Doctor</h3>
            <form onSubmit={handleAddDoctor}>
              <div className="form-grid">
                <div className="form-group">
                  <label>Full Name</label>
                  <input required placeholder="Dr. John Smith" value={doctorForm.name}
                    onChange={e => setDoctorForm({ ...doctorForm, name: e.target.value })} />
                </div>
                <div className="form-group">
                  <label>Email</label>
                  <input required type="email" placeholder="doctor@hospital.com" value={doctorForm.email}
                    onChange={e => setDoctorForm({ ...doctorForm, email: e.target.value })} />
                </div>
                <div className="form-group">
                  <label>Password</label>
                  <input required type="password" placeholder="Temporary password" value={doctorForm.password}
                    onChange={e => setDoctorForm({ ...doctorForm, password: e.target.value })} />
                </div>
                <div className="form-group">
                  <label>Specialization</label>
                  <input placeholder="e.g. Cardiology" value={doctorForm.specialization}
                    onChange={e => setDoctorForm({ ...doctorForm, specialization: e.target.value })} />
                </div>
                <div className="form-group full">
                  <label>Department</label>
                  <input placeholder="e.g. Heart & Vascular" value={doctorForm.department}
                    onChange={e => setDoctorForm({ ...doctorForm, department: e.target.value })} />
                </div>
                <div className="form-group full">
                  <label>Consultation Mode</label>
                  <select required value={doctorForm.mode} onChange={e => setDoctorForm({ ...doctorForm, mode: e.target.value })}>
                    <option value="ONLINE">Online (Video Consultation)</option>
                    <option value="OFFLINE">Offline (In-Clinic)</option>
                  </select>
                </div>
              </div>
              <div className="modal-actions">
                <button type="button" className="btn btn-outline" onClick={() => setShowAddDoctor(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary" disabled={loading}>
                  {loading ? "Adding..." : "Add Doctor"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

// Shared sub-component
function AppointmentTable({ appointments, onCancel, showCancel }) {
  if (appointments.length === 0)
    return <div className="empty-state"><div className="empty-icon">📋</div><p>No appointments found.</p></div>;

  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            <th>#</th><th>Patient</th><th>Doctor</th>
            <th>Date</th><th>Time</th><th>Mode/Location</th><th>Status</th>
            {showCancel && <th>Action</th>}
          </tr>
        </thead>
        <tbody>
          {appointments.map((a, i) => (
            <tr key={a.id}>
              <td>{i + 1}</td>
              <td>{a.patientName}</td>
              <td>{a.doctorName}</td>
              <td>{a.appointmentDate}</td>
              <td>{a.startTime} – {a.endTime}</td>
              <td>
                {a.mode === "ONLINE" ? (
                  <a href={a.videoLink} target="_blank" rel="noreferrer" style={{ color: "var(--primary)" }}>Join Video</a>
                ) : (
                  <span style={{ fontSize: "0.85rem" }}>{a.clinicAddress || "In-person"}</span>
                )}
              </td>
              <td><StatusBadge status={a.status} /></td>
              {showCancel && (
                <td>
                  {(a.status === "BOOKED" || a.status === "CONFIRMED") && (
                    <button className="btn btn-danger btn-sm" onClick={() => onCancel(a.id)}>Cancel</button>
                  )}
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function StatusBadge({ status }) {
  const map = {
    BOOKED: "badge-booked", CONFIRMED: "badge-confirmed",
    COMPLETED: "badge-completed", CANCELLED: "badge-cancelled"
  };
  return <span className={`badge ${map[status] || ""}`}>{status}</span>;
}

export default AdminDashboard;