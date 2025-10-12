// Initialize page
document.addEventListener("DOMContentLoaded", () => {
  const currentPage = window.location.pathname.split("/").pop();
  if (currentPage === "employees.html" || currentPage === "") {
    // Initial load is handled by Thymeleaf; no need to call loadEmployees here
  }
  if (currentPage === "index.html") {
    updateDashboardStats();
  }
  if (currentPage === "profileAdmin.html") {
    loadProfileData(); // Refresh data in case session data is stale
  }
});

// Dashboard stats function
function updateDashboardStats() {
  fetch('/admin/employees/api', {
    headers: {
      'Content-Type': 'application/json',
    }
  })
    .then(response => {
      if (!response.ok) {
        throw new Error('Lỗi khi tải dữ liệu dashboard');
      }
      return response.json();
    })
    .then(employees => {
      const totalEmployees = employees.length;
      const activeEmployees = employees.filter(emp => emp.active).length;
      const departments = [...new Set(employees.map(emp => emp.department || 'N/A'))].length;

      if (document.getElementById("totalEmployees")) {
        document.getElementById("totalEmployees").textContent = totalEmployees;
      }
      if (document.getElementById("activeEmployees")) {
        document.getElementById("activeEmployees").textContent = activeEmployees;
      }
      if (document.getElementById("totalDepartments")) {
        document.getElementById("totalDepartments").textContent = departments;
      }
    })
    .catch(error => {
      console.error('Error loading dashboard stats:', error);
      alert('Lỗi khi tải dữ liệu dashboard!');
    });
}

// Load employees
function loadEmployees() {
  fetch('/admin/employees/api', {
    headers: {
      'Content-Type': 'application/json',
    }
  })
    .then(response => {
      if (!response.ok) {
        throw new Error('Lỗi khi tải danh sách nhân viên');
      }
      return response.json();
    })
    .then(employees => {
      const tbody = document.getElementById("employeeTableBody");
      if (!tbody) return;

      tbody.innerHTML = "";
      employees.forEach(employee => {
        if (!employee.id || isNaN(employee.id)) {
          console.warn('Invalid employee ID:', employee);
          return;
        }
        const row = document.createElement("tr");
        row.innerHTML = `
                    <td><img src="${employee.avatar || '/images/default-avatar.png'}" alt="Ảnh nhân viên" class="table-employee-image"></td>
                    <td>${employee.fullname || 'N/A'}</td>
                    <td>${employee.email}</td>
                    <td>
                        <span class="status-badge ${employee.active ? 'status-active' : 'status-inactive'}">
                            ${employee.active ? 'Hoạt động' : 'Không hoạt động'}
                        </span>
                    </td>
                    <td>
                        <button class="btn btn-secondary" onclick="viewEmployee(${employee.id})" style="margin-right: 0.5rem;">
                            <i class="fas fa-eye"></i>
                        </button>
                        <button class="btn btn-secondary" onclick="editEmployee(${employee.id})" style="margin-right: 0.5rem;">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-danger" onclick="deleteEmployee(${employee.id})">
                            <i class="fas fa-trash"></i>
                        </button>
                    </td>
                `;
        tbody.appendChild(row);
      });
    })
    .catch(error => {
      console.error('Error loading employees:', error);
      alert('Lỗi khi tải danh sách nhân viên!');
    });
}

// Open employee modal
let editingEmployeeId = null;

function openEmployeeModal(employeeId = null) {
  const modal = document.getElementById("employeeModal");
  const form = document.getElementById("employeeForm");
  const title = document.getElementById("modalTitle");

  editingEmployeeId = employeeId;

  if (employeeId) {
    if (!Number.isInteger(Number(employeeId))) {
      console.error('Invalid employee ID:', employeeId);
      alert('ID nhân viên không hợp lệ!');
      return;
    }
    fetch(`/admin/employees/api/${employeeId}`, {
      headers: {
        'Content-Type': 'application/json',
      }
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('Lỗi khi tải thông tin nhân viên');
        }
        return response.json();
      })
      .then(employee => {
        title.textContent = "Chỉnh sửa nhân viên";
        document.getElementById("employeeName").value = employee.fullname || '';
        document.getElementById("employeeEmail").value = employee.email;
        modal.style.display = "block";
      })
      .catch(error => {
        console.error('Error fetching employee:', error);
        alert('Lỗi khi tải thông tin nhân viên!');
      });
  } else {
    title.textContent = "Thêm nhân viên mới";
    form.reset();
    modal.style.display = "block";
  }
}

// Close employee modal
function closeEmployeeModal() {
  const modal = document.getElementById("employeeModal");
  modal.style.display = "none";
  editingEmployeeId = null;
}

// Save employee
function saveEmployee(event) {
  event.preventDefault();

  const formData = new FormData(event.target);
  const password = formData.get("password");
  const confirmPassword = formData.get("confirmPassword");

  if (password !== confirmPassword) {
    alert("Mật khẩu và xác nhận mật khẩu không khớp!");
    return;
  }

  if (password && !/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{6,}$/.test(password)) {
    alert("Mật khẩu phải có ít nhất 6 ký tự gồm chữ hoa, số và ký tự đặc biệt!");
    return;
  }

  const employeeData = {
    username: formData.get("email").split('@')[0],
    fullname: formData.get("name"),
    email: formData.get("email"),
    phone: formData.get("phone"),
    password: password || undefined,
    role: formData.get("role") || "USER"
  };

  const url = editingEmployeeId ? `/admin/employees/api/${editingEmployeeId}` : '/admin/employees/api';
  const method = editingEmployeeId ? 'PUT' : 'POST';

  fetch(url, {
    method: method,
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(employeeData)
  })
    .then(response => {
      if (!response.ok) {
        return response.json().then(error => { throw new Error(error.message || 'Lỗi khi lưu nhân viên'); });
      }
      return response.json();
    })
    .then(() => {
      loadEmployees();
      closeEmployeeModal();
      alert(editingEmployeeId ? "Cập nhật nhân viên thành công!" : "Thêm nhân viên thành công!");
    })
    .catch(error => {
      console.error('Error saving employee:', error);
      alert(error.message || 'Lỗi khi lưu nhân viên!');
    });
}

// Edit employee
function editEmployee(id) {
  if (!Number.isInteger(Number(id))) {
    console.error('Invalid employee ID:', id);
    alert('ID nhân viên không hợp lệ!');
    return;
  }
  openEmployeeModal(id);
}

// Delete employee
function deleteEmployee(id) {
  if (!Number.isInteger(Number(id))) {
    console.error('Invalid employee ID:', id);
    alert('ID nhân viên không hợp lệ!');
    return;
  }
  if (confirm("Bạn có chắc chắn muốn xóa nhân viên này?")) {
    fetch(`/admin/employees/api/${id}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
      }
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('Lỗi khi xóa nhân viên');
        }
        loadEmployees();
        alert("Xóa nhân viên thành công!");
      })
      .catch(error => {
        console.error('Error deleting employee:', error);
        alert('Lỗi khi xóa nhân viên!');
      });
  }
}

// Search employees
function searchEmployees() {
  const searchTerm = document.getElementById("searchInput").value.toLowerCase();
  fetch('/admin/employees/api')
    .then(response => {
      if (!response.ok) {
        throw new Error('Lỗi khi tải danh sách nhân viên');
      }
      return response.json();
    })
    .then(employees => {
      const filteredEmployees = employees.filter(employee =>
        (employee.fullname && employee.fullname.toLowerCase().includes(searchTerm)) ||
        employee.email.toLowerCase().includes(searchTerm)
      );
      const tbody = document.getElementById("employeeTableBody");
      tbody.innerHTML = "";
      filteredEmployees.forEach(employee => {
        if (!employee.id || isNaN(employee.id)) {
          console.warn('Invalid employee ID:', employee);
          return;
        }
        const row = document.createElement("tr");
        row.innerHTML = `
                    <td><img src="${employee.avatar || '/images/default-avatar.png'}" alt="Ảnh nhân viên" class="table-employee-image"></td>
                    <td>${employee.fullname || 'N/A'}</td>
                    <td>${employee.email}</td>
                    <td>
                        <span class="status-badge ${employee.active ? 'status-active' : 'status-inactive'}">
                            ${employee.active ? 'Hoạt động' : 'Không hoạt động'}
                        </span>
                    </td>
                    <td>
                        <button class="btn btn-secondary" onclick="viewEmployee(${employee.id})" style="margin-right: 0.5rem;">
                            <i class="fas fa-eye"></i>
                        </button>
                        <button class="btn btn-secondary" onclick="editEmployee(${employee.id})" style="margin-right: 0.5rem;">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-danger" onclick="deleteEmployee(${employee.id})">
                            <i class="fas fa-trash"></i>
                        </button>
                    </td>
                `;
        tbody.appendChild(row);
      });
    })
    .catch(error => {
      console.error('Error searching employees:', error);
      alert('Lỗi khi tìm kiếm nhân viên!');
    });
}

// View employee details
let currentEmployeeId = null;

function viewEmployee(employeeId) {
  currentEmployeeId = employeeId;
  fetch(`/admin/employees/api/${employeeId}`)
    .then(response => {
      if (!response.ok) {
        throw new Error('Không thể lấy thông tin nhân viên');
      }
      return response.json();
    })
    .then(employee => {
      document.getElementById('viewEmployeeName').textContent = employee.fullname || 'N/A';
      document.getElementById('viewEmployeeEmail').textContent = employee.email || 'N/A';
      document.getElementById('viewEmployeeStatus').value = employee.active ? 'true' : 'false';
      document.getElementById('viewEmployeeImage').src = employee.avatar || '/images/default-avatar.png';
      document.getElementById('viewEmployeeModal').style.display = 'block';
    })
    .catch(error => {
      console.error('Error:', error);
      alert('Đã xảy ra lỗi khi lấy thông tin nhân viên!');
    });
}

// Update employee status
function updateEmployeeStatus() {
  const statusSelect = document.getElementById('viewEmployeeStatus');
  const newStatus = statusSelect.value === 'true';

  const updatedEmployee = {
    active: newStatus
  };

  fetch(`/admin/employees/api/${currentEmployeeId}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(updatedEmployee)
  })
    .then(response => {
      if (!response.ok) {
        throw new Error('Không thể cập nhật trạng thái nhân viên');
      }
      return response.json();
    })
    .then(updatedEmployee => {
      alert('Cập nhật trạng thái thành công!');
      searchEmployees();
    })
    .catch(error => {
      console.error('Error:', error);
      alert('Đã xảy ra lỗi khi cập nhật trạng thái!');
    });
}

// Close view employee modal
function closeViewEmployeeModal() {
  document.getElementById('viewEmployeeModal').style.display = 'none';
  currentEmployeeId = null;
}

// Profile Management Functions
let isEditMode = false;

function toggleEditMode() {
  isEditMode = !isEditMode;
  const form = document.getElementById("profileForm");
  const inputs = form.querySelectorAll("input, textarea");
  const editButton = document.getElementById("editButtonText");
  const formActions = document.getElementById("profileFormActions");
  const changeAvatarBtn = document.getElementById("changeAvatarBtn");

  inputs.forEach(input => {
    if (input.name !== "joinDate") {
      input.disabled = !isEditMode;
    }
  });

  changeAvatarBtn.disabled = !isEditMode;
  editButton.textContent = isEditMode ? "Hủy" : "Chỉnh sửa";
  formActions.style.display = isEditMode ? "flex" : "none";

  if (!isEditMode) {
    loadProfileData();
  }
}

function loadProfileData() {
  fetch('/api/current-user', {
    headers: {
      'Content-Type': 'application/json',
    }
  })
    .then(response => {
      if (!response.ok) {
        throw new Error('Lỗi khi tải thông tin hồ sơ');
      }
      return response.json();
    })
    .then(user => {
      document.getElementById("profileName").value = user.fullname || 'Admin User';
      document.getElementById("profileEmail").value = user.email || 'admin@musicmanager.com';
      document.getElementById("profileJoinDate").value = user.createdAt ? user.createdAt.split('T')[0] : '2023-01-15';
      document.getElementById("profileBio").value = user.bio || '';
      document.getElementById("profileAvatarLarge").src = user.avatar || '/images/default-avatar.png';
    })
    .catch(error => {
      console.error('Error loading profile data:', error);
      alert('Lỗi khi tải thông tin hồ sơ!');
    });
}

function cancelEdit() {
  toggleEditMode();
}

function saveProfile(event) {
  event.preventDefault();

  const formData = new FormData(event.target);
  const profileData = {
    fullname: formData.get("name"),
    email: formData.get("email"),
    bio: formData.get("bio")
  };

  fetch('/api/current-user', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(profileData)
  })
    .then(response => {
      if (!response.ok) {
        throw new Error('Lỗi khi cập nhật hồ sơ');
      }
      return response.json();
    })
    .then(() => {
      toggleEditMode();
      alert("Cập nhật hồ sơ thành công!");
    })
    .catch(error => {
      console.error('Error saving profile:', error);
      alert('Lỗi khi cập nhật hồ sơ!');
    });
}

function changeAvatar() {
  const input = document.createElement('input');
  input.type = 'file';
  input.accept = 'image/*';
  input.onchange = function (event) {
    const file = event.target.files[0];
    if (!file) return;

    if (file.size > 5 * 1024 * 1024) {
      alert('Kích thước ảnh không được vượt quá 5MB!');
      return;
    }

    const formData = new FormData();
    formData.append('avatar', file);

    fetch('/api/current-user/avatar', {
      method: 'POST',
      body: formData
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('Lỗi khi tải lên ảnh đại diện');
        }
        return response.json();
      })
      .then(data => {
        document.getElementById("profileAvatarLarge").src = data.avatar || '/images/default-avatar.png';
        alert('Đổi ảnh đại diện thành công!');
      })
      .catch(error => {
        console.error('Error uploading avatar:', error);
        alert('Lỗi khi tải lên ảnh đại diện!');
      });
  };
  input.click();
}

function changePassword(event) {
  event.preventDefault();

  const currentPassword = document.getElementById("currentPassword").value;
  const newPassword = document.getElementById("newPassword").value;
  const confirmPassword = document.getElementById("confirmPassword").value;

  if (newPassword !== confirmPassword) {
    alert("Mật khẩu mới và xác nhận mật khẩu không khớp!");
    return;
  }

  if (!/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{6,}$/.test(newPassword)) {
    alert("Mật khẩu mới phải có ít nhất 6 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt!");
    return;
  }

  fetch('/api/change-password', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      currentPassword,
      newPassword
    })
  })
    .then(response => {
      if (!response.ok) {
        throw new Error('Lỗi khi đổi mật khẩu');
      }
      document.getElementById("passwordForm").reset();
      alert("Đổi mật khẩu thành công!");
    })
    .catch(error => {
      console.error('Error changing password:', error);
      alert('Lỗi khi đổi mật khẩu!');
    });
}

function logout() {
  if (confirm("Bạn có chắc chắn muốn đăng xuất?")) {
    fetch('/logout', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      }
    })
      .then(() => {
        alert("Đã đăng xuất thành công!");
        window.location.href = "/Dangnhap";
      })
      .catch(error => {
        console.error('Error logging out:', error);
        alert('Lỗi khi đăng xuất!');
      });
  }
}

// Close modal when clicking outside
window.onclick = (event) => {
  const employeeModal = document.getElementById("employeeModal");
  const viewEmployeeModal = document.getElementById("viewEmployeeModal");
  if (event.target === employeeModal) {
    closeEmployeeModal();
  }
  if (event.target === viewEmployeeModal) {
    closeViewEmployeeModal();
  }
};