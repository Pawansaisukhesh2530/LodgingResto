document.addEventListener('DOMContentLoaded', () => {
    animateCounters();
    renderDashboardCharts();
    bindDeleteModal();
    autoDismissToasts();
    setupLocalSearch();
    bindButtonRipples();
    setupScrollReveals();
});

/* ==========================================================================
   SMOOTH STATISTICAL COUNTER INCREMENTS
   ========================================================================== */
function animateCounters() {
    document.querySelectorAll('.counter-value').forEach((el) => {
        const raw = el.dataset.count;
        const target = Number(String(raw).replace(/[^0-9.-]/g, '')) || 0;
        const isCurrency = el.textContent.trim().startsWith('\u20B9');
        const start = 0;
        const duration = 1200;
        const begin = performance.now();

        function easeOutCubic(x) {
            return 1 - Math.pow(1 - x, 3);
        }

        function step(now) {
            const elapsed = now - begin;
            const progress = Math.min(elapsed / duration, 1);
            const easeProgress = easeOutCubic(progress);
            const value = start + (target - start) * easeProgress;

            if (isCurrency) {
                el.textContent = `\u20B9${value.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
            } else {
                el.textContent = Math.round(value).toLocaleString('en-IN');
            }

            if (progress < 1) {
                requestAnimationFrame(step);
            }
        }

        if (!Number.isNaN(target) && raw !== undefined) {
            requestAnimationFrame(step);
        }
    });
}

/* ==========================================================================
   DASHBOARD CHARTS ENGINE (6 VISUALIZATIONS)
   ========================================================================== */
function renderDashboardCharts() {
    if (typeof Chart === 'undefined') return;

    // Helper to get labels and numeric values from a canvas dataset
    function getChartData(canvas) {
        if (!canvas) return null;
        const labels = String(canvas.dataset.labels || '').split('|');
        const values = String(canvas.dataset.values || '').split('|').map(v => Number(v) || 0);
        return { labels, values };
    }

    // Chart 1: Monthly Revenue Trend (Line Chart)
    const revenueCanvas = document.getElementById('revenueChart');
    if (revenueCanvas) {
        const ctx = revenueCanvas.getContext('2d');
        const data = getChartData(revenueCanvas);
        if (data && data.labels[0] !== "") {
            // Create nice gradient fill under the line
            const gradient = ctx.createLinearGradient(0, 0, 0, 240);
            gradient.addColorStop(0, 'rgba(59, 130, 246, 0.12)');
            gradient.addColorStop(1, 'rgba(59, 130, 246, 0.00)');

            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: data.labels,
                    datasets: [{
                        label: 'Gross revenue',
                        data: data.values,
                        borderColor: '#3b82f6',
                        backgroundColor: gradient,
                        borderWidth: 3,
                        tension: 0.35,
                        fill: true,
                        pointBackgroundColor: '#3b82f6',
                        pointHoverRadius: 7
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { display: false }
                    },
                    scales: {
                        x: { grid: { color: 'rgba(15, 23, 42, 0.03)' }, ticks: { color: '#64748b' } },
                        y: { grid: { color: 'rgba(15, 23, 42, 0.03)' }, ticks: { color: '#64748b' } }
                    }
                }
            });
        }
    }

    // Chart 2: Room Occupancy Statistics (Pie/Doughnut Chart)
    const roomCanvas = document.getElementById('roomChart');
    if (roomCanvas) {
        const ctx = roomCanvas.getContext('2d');
        const data = getChartData(roomCanvas);
        if (data) {
            const total = data.values.reduce((sum, val) => sum + val, 0);
            const centerTextPlugin = {
                id: 'centerText',
                afterDraw(chart) {
                    const { ctx, chartArea: { left, right, top, bottom } } = chart;
                    ctx.save();
                    const centerX = (left + right) / 2;
                    const centerY = (top + bottom) / 2;
                    ctx.font = "bold 10px 'Inter', sans-serif";
                    ctx.fillStyle = '#64748b';
                    ctx.textAlign = 'center';
                    ctx.textBaseline = 'middle';
                    ctx.fillText('PORTFOLIO', centerX, centerY - 14);
                    ctx.font = "800 24px 'Poppins', sans-serif";
                    ctx.fillStyle = '#0f172a';
                    ctx.fillText(total, centerX, centerY + 10);
                    ctx.restore();
                }
            };

            new Chart(ctx, {
                type: 'doughnut',
                data: {
                    labels: data.labels,
                    datasets: [{
                        data: data.values,
                        backgroundColor: ['#10b981', '#ef4444', '#3b82f6', '#f59e0b'],
                        borderColor: '#ffffff',
                        borderWidth: 3,
                        hoverOffset: 6,
                        borderRadius: 4
                    }]
                },
                options: {
                    cutout: '74%',
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { position: 'bottom', labels: { usePointStyle: true, boxWidth: 6, color: '#475569', font: { size: 11 } } }
                    }
                },
                plugins: [centerTextPlugin]
            });
        }
    }

    // Chart 3: Reservation Status Distribution (Doughnut Chart)
    const resCanvas = document.getElementById('resChart');
    if (resCanvas) {
        const ctx = resCanvas.getContext('2d');
        const data = getChartData(resCanvas);
        if (data && data.labels[0] !== "") {
            new Chart(ctx, {
                type: 'doughnut',
                data: {
                    labels: data.labels,
                    datasets: [{
                        data: data.values,
                        backgroundColor: ['#10b981', '#f59e0b', '#64748b'],
                        borderColor: '#ffffff',
                        borderWidth: 2,
                        borderRadius: 3
                    }]
                },
                options: {
                    cutout: '65%',
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { position: 'bottom', labels: { usePointStyle: true, boxWidth: 6, color: '#475569', font: { size: 11 } } }
                    }
                }
            });
        }
    }

    // Chart 4: Restaurant Sales Analytics (Bar Chart)
    const restCanvas = document.getElementById('restaurantChart');
    if (restCanvas) {
        const ctx = restCanvas.getContext('2d');
        const data = getChartData(restCanvas);
        if (data && data.labels[0] !== "") {
            // Elegant gradient bar coloring
            const barGradient = ctx.createLinearGradient(0, 0, 0, 220);
            barGradient.addColorStop(0, '#d4af37');
            barGradient.addColorStop(1, '#aa8410');

            new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: data.labels,
                    datasets: [{
                        label: 'Restaurant Sales (INR)',
                        data: data.values,
                        backgroundColor: barGradient,
                        borderRadius: 6,
                        borderWidth: 0,
                        maxBarThickness: 32
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { display: false }
                    },
                    scales: {
                        x: { grid: { display: false }, ticks: { color: '#64748b' } },
                        y: { grid: { color: 'rgba(15, 23, 42, 0.03)' }, ticks: { color: '#64748b' } }
                    }
                }
            });
        }
    }

    // Chart 5: API Usage Analytics (Area Chart)
    const apiCanvas = document.getElementById('apiChart');
    if (apiCanvas) {
        const ctx = apiCanvas.getContext('2d');
        const data = getChartData(apiCanvas);
        if (data && data.labels[0] !== "") {
            const apiGradient = ctx.createLinearGradient(0, 0, 0, 220);
            apiGradient.addColorStop(0, 'rgba(16, 185, 129, 0.12)');
            apiGradient.addColorStop(1, 'rgba(16, 185, 129, 0.00)');

            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: data.labels,
                    datasets: [{
                        label: 'API Request Rate',
                        data: data.values,
                        borderColor: '#10b981',
                        backgroundColor: apiGradient,
                        borderWidth: 3,
                        tension: 0.3,
                        fill: true,
                        pointBackgroundColor: '#10b981'
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { display: false }
                    },
                    scales: {
                        x: { grid: { color: 'rgba(15, 23, 42, 0.03)' }, ticks: { color: '#64748b' } },
                        y: { grid: { color: 'rgba(15, 23, 42, 0.03)' }, ticks: { color: '#64748b' } }
                    }
                }
            });
        }
    }

    // Chart 6: Inventory Stock Status (Horizontal Bar Chart)
    const invCanvas = document.getElementById('inventoryChart');
    if (invCanvas) {
        const ctx = invCanvas.getContext('2d');
        const data = getChartData(invCanvas);
        if (data && data.labels[0] !== "") {
            const invGradient = ctx.createLinearGradient(0, 0, 300, 0);
            invGradient.addColorStop(0, '#8b5cf6');
            invGradient.addColorStop(1, '#a78bfa');

            new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: data.labels,
                    datasets: [{
                        label: 'Stock Quantity',
                        data: data.values,
                        backgroundColor: invGradient,
                        borderRadius: 5,
                        maxBarThickness: 16
                    }]
                },
                options: {
                    indexAxis: 'y',
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { display: false }
                    },
                    scales: {
                        x: { grid: { color: 'rgba(15, 23, 42, 0.03)' }, ticks: { color: '#64748b' } },
                        y: { grid: { display: false }, ticks: { color: '#64748b' } }
                    }
                }
            });
        }
    }
}

/* ==========================================================================
   DELETE MODAL ACTIONS
   ========================================================================== */
function bindDeleteModal() {
    const modal = document.getElementById('deleteRoomModal');
    if (!modal) return;

    modal.addEventListener('show.bs.modal', (event) => {
        const button = event.relatedTarget;
        if (!button) return;
        const roomName = button.getAttribute('data-room-name') || 'this room';
        const deleteUrl = button.getAttribute('data-delete-url') || '#';
        modal.querySelector('#deleteRoomLabel').textContent = roomName;
        modal.querySelector('#deleteRoomForm').setAttribute('action', deleteUrl);
    });
}

/* ==========================================================================
   AUTO-DISMISS DISPATCHER TOASTS
   ========================================================================== */
function autoDismissToasts() {
    document.querySelectorAll('.toast.show').forEach((toast) => {
        setTimeout(() => {
            const instance = bootstrap.Toast.getOrCreateInstance(toast);
            if (instance) instance.hide();
        }, 4500);
    });
}

/* ==========================================================================
   INSTANT LOCAL TABLE SEARCH FILTERING
   ========================================================================== */
function setupLocalSearch() {
    const searchInputs = document.querySelectorAll('.topbar-search input');
    searchInputs.forEach(input => {
        input.addEventListener('keyup', (e) => {
            const term = e.target.value.toLowerCase().trim();
            const rows = document.querySelectorAll('.premium-table tbody tr');
            
            if (rows.length === 0 || rows[0].querySelector('.empty-state')) return;

            rows.forEach(row => {
                let match = false;
                const cells = row.querySelectorAll('td');
                cells.forEach(cell => {
                    if (cell.textContent.toLowerCase().includes(term)) {
                        match = true;
                    }
                });
                row.style.display = match ? '' : 'none';
            });
        });
    });
}

/* ==========================================================================
   BUTTON CLICK RIPPLE EFFECTS
   ========================================================================== */
function bindButtonRipples() {
    const buttons = document.querySelectorAll('.btn, .btn-premium, .btn-action, .btn-edit, .btn-delete, .nav-link-item');
    buttons.forEach(btn => {
        btn.classList.add('btn-ripple');
        btn.addEventListener('click', function(e) {
            const rect = this.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;

            const ripple = document.createElement('span');
            ripple.className = 'ripple-circle';
            ripple.style.left = `${x}px`;
            ripple.style.top = `${y}px`;

            const existing = this.querySelectorAll('.ripple-circle');
            existing.forEach(r => r.remove());

            this.appendChild(ripple);

            setTimeout(() => {
                ripple.remove();
            }, 600);
        });
    });
}

/* ==========================================================================
   INTERSECTION OBSERVER SCROLL REVEALS
   ========================================================================== */
function setupScrollReveals() {
    const cards = document.querySelectorAll('.stat-card, .stat-mini-card, .glass-card, .premium-table-wrap');
    if (cards.length === 0) return;
    
    const observerOptions = {
        threshold: 0.05,
        rootMargin: '0px 0px -40px 0px'
    };

    const observer = new IntersectionObserver((entries, obs) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('active');
                obs.unobserve(entry.target);
            }
        });
    }, observerOptions);

    cards.forEach(card => {
        card.classList.add('scroll-reveal');
        observer.observe(card);
    });
}
