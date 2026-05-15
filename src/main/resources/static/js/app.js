document.addEventListener('DOMContentLoaded', () => {
    animateCounters();
    renderRoomChart();
    bindDeleteModal();
    autoDismissToasts();
});

function animateCounters() {
    document.querySelectorAll('.counter-value').forEach((el) => {
        const raw = el.dataset.count;
        const target = Number(String(raw).replace(/[^0-9.-]/g, '')) || 0;
        const isCurrency = el.textContent.trim().startsWith('$');
        const start = 0;
        const duration = 900;
        const begin = performance.now();

        function step(now) {
            const progress = Math.min((now - begin) / duration, 1);
            const value = start + (target - start) * progress;
            if (isCurrency) {
                el.textContent = `$${value.toFixed(2)}`;
            } else {
                el.textContent = Math.round(value).toString();
            }
            if (progress < 1) requestAnimationFrame(step);
        }

        if (!Number.isNaN(target) && raw !== undefined) {
            requestAnimationFrame(step);
        }
    });
}

function renderRoomChart() {
    const canvas = document.getElementById('roomChart');
    if (!canvas || typeof Chart === 'undefined') return;

    const ctx = canvas.getContext('2d');
    const labels = String(canvas.dataset.labels || 'Available|Occupied|Reserved|Maintenance').split('|');
    const values = String(canvas.dataset.values || '0|0|0|0').split('|').map((value) => Number(value) || 0);
    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels,
            datasets: [{
                data: values,
                backgroundColor: ['#16a34a', '#ef4444', '#3b82f6', '#f59e0b'],
                borderWidth: 0,
                hoverOffset: 8
            }]
        },
        options: {
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        usePointStyle: true,
                        pointStyle: 'circle',
                        boxWidth: 10,
                        color: '#334155'
                    }
                }
            },
            cutout: '72%',
            responsive: true,
            maintainAspectRatio: false
        }
    });
}

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

function autoDismissToasts() {
    document.querySelectorAll('.toast.show').forEach((toast) => {
        setTimeout(() => {
            const instance = bootstrap.Toast.getOrCreateInstance(toast);
            instance.hide();
        }, 4500);
    });
}


