document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.btn-danger').forEach(button => {
        button.addEventListener('click', function(e) {
            if (!confirm('Voulez-vous vraiment supprimer cette tâche ?')) {
                e.preventDefault();
            }
        });
    });

    document.querySelectorAll('.btn-assign').forEach(button => {
        button.addEventListener('click', function(e) {
            const taskId = this.closest('form').querySelector('input[name="taskId"]').value;
            console.log(`Assigning task with ID: ${taskId} to the current user.`);
        });
    });
});
