package com.diao.taskflowapi.services.impls;

import com.diao.taskflowapi.dtos.requests.ProjectRequest;
import com.diao.taskflowapi.dtos.responses.ProjectResponse;
import com.diao.taskflowapi.entities.Project;
import com.diao.taskflowapi.entities.User;
import com.diao.taskflowapi.exceptions.AccessDeniedToResourceException;
import com.diao.taskflowapi.exceptions.ResourceNotFoundException;
import com.diao.taskflowapi.mappers.manuals.ProjectManualMapper;
import com.diao.taskflowapi.repositories.ProjectRepository;
import com.diao.taskflowapi.repositories.UserRepository;
import com.diao.taskflowapi.services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation des operations metier sur les projets.
 * <p>
 * Utilise le mapper MANUEL ({@link ProjectManualMapper}) a titre de
 * demonstration / comparaison avec le mapper MapStruct utilise ailleurs
 * (ex: {@link com.diao.taskflowapi.services.impls.AuthServiceImpl}).
 */
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectManualMapper projectMapper;

    @Override
    @Transactional
    public ProjectResponse create(ProjectRequest request, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> ResourceNotFoundException.of("Utilisateur", ownerId));

        Project project = projectMapper.toEntity(request, owner);
        Project saved = projectRepository.save(project);

        return projectMapper.toSummaryResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectResponse> findMyProjects(Long ownerId, Pageable pageable) {
        return projectRepository.findByOwnerId(ownerId, pageable)
                .map(projectMapper::toSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse findById(Long projectId, Long currentUserId, boolean isAdmin) {
        Project project = getProjectOrThrow(projectId);
        checkOwnershipOrAdmin(project, currentUserId, isAdmin);

        return projectMapper.toDetailedResponse(project);
    }

    @Override
    @Transactional
    public ProjectResponse update(Long projectId, ProjectRequest request, Long currentUserId, boolean isAdmin) {
        Project project = getProjectOrThrow(projectId);
        checkOwnershipOrAdmin(project, currentUserId, isAdmin);

        projectMapper.updateEntity(project, request);
        Project saved = projectRepository.save(project);

        return projectMapper.toSummaryResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long projectId, Long currentUserId, boolean isAdmin) {
        Project project = getProjectOrThrow(projectId);
        checkOwnershipOrAdmin(project, currentUserId, isAdmin);

        projectRepository.delete(project);
    }

    // ===================== Helpers =====================

    private Project getProjectOrThrow(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> ResourceNotFoundException.of("Projet", projectId));
    }

    /**
     * Verifie que l'utilisateur courant est soit le proprietaire du projet,
     * soit un administrateur. Sinon, leve une exception 403.
     */
    private void checkOwnershipOrAdmin(Project project, Long currentUserId, boolean isAdmin) {
        if (isAdmin) {
            return;
        }
        if (!project.getOwner().getId().equals(currentUserId)) {
            throw new AccessDeniedToResourceException(
                    "Vous n'etes pas autorise a acceder a ce projet"
            );
        }
    }
}